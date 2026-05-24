package com.kernelpanic.projeto_service.servicos;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.kernelpanic.projeto_service.dtos.EvolucaoFinanceiraProjetoDTO;
import com.kernelpanic.projeto_service.dtos.AlertaIndicadorFinanceiroDTO;
import com.kernelpanic.projeto_service.dtos.HorasAprovadasAgregadoDTO;
import com.kernelpanic.projeto_service.dtos.HorasAprovadasEvolucaoDTO;
import com.kernelpanic.projeto_service.dtos.IndicadorFinanceiroProjetoDTO;
import com.kernelpanic.projeto_service.dtos.UsuarioFinanceiroDTO;
import com.kernelpanic.projeto_service.entidades.Projeto;
import com.kernelpanic.projeto_service.enums.PeriodicidadeFinanceira;
import com.kernelpanic.projeto_service.enums.StatusFinanceiroProjeto;
import com.kernelpanic.projeto_service.enums.TipoContratacao;
import com.kernelpanic.projeto_service.repositorios.ProjetoRepositorio;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class IndicadorFinanceiroProjetoServico {

    private static final ZoneId ZONE_ID = ZoneId.of("America/Sao_Paulo");
    private static final BigDecimal CEM = BigDecimal.valueOf(100);
    private static final BigDecimal HORAS_CLT_MES = BigDecimal.valueOf(220);
    private static final BigDecimal HORAS_PJ_FIXAS_MES = BigDecimal.valueOf(160);
    private static final BigDecimal ZERO = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);

    private final ProjetoRepositorio projetoRepositorio;
    private final RestTemplate restTemplate = criarRestTemplateComTimeout();

    @Value("${integracoes.apontamento-horas.url:http://localhost:8084}")
    private String apontamentoHorasUrl;

    @Value("${integracoes.usuario.url:http://localhost:8083}")
    private String usuarioUrl;

    @Value("${internal.api-key:}")
    private String internalApiKey;

    public List<IndicadorFinanceiroProjetoDTO> obterIndicadores(
            PeriodicidadeFinanceira periodicidade,
            Integer ano,
            Integer mes,
            LocalDate dataReferencia) {

        Periodo periodo = resolverPeriodo(periodicidade, ano, mes, dataReferencia);
        List<Projeto> projetos = projetoRepositorio.findAll();

        List<HorasAprovadasAgregadoDTO> horasAgregadas = buscarHorasAgregadas(periodo);
        List<HorasAprovadasEvolucaoDTO> horasEvolucao = buscarHorasEvolucao(periodo);
        Map<Long, UsuarioFinanceiroDTO> usuarios = buscarUsuariosFinanceiros(horasAgregadas, horasEvolucao);

        Map<Long, BigDecimal> custoPorProjeto = calcularCustoPorProjeto(horasAgregadas, usuarios);
        Map<Long, Map<LocalDate, BigDecimal>> custoDiarioPorProjeto = calcularCustoDiarioPorProjeto(horasEvolucao, usuarios);
        Map<Long, List<AlertaIndicadorFinanceiroDTO>> alertasPorProjeto = calcularAlertasPorProjeto(horasAgregadas, usuarios);

        return projetos.stream()
                .map(projeto -> montarIndicador(projeto, periodicidade, periodo, custoPorProjeto, custoDiarioPorProjeto, alertasPorProjeto))
                .sorted(Comparator.comparing(IndicadorFinanceiroProjetoDTO::getPercentualConsumido).reversed())
                .collect(Collectors.toList());
    }

    private IndicadorFinanceiroProjetoDTO montarIndicador(
            Projeto projeto,
            PeriodicidadeFinanceira periodicidade,
            Periodo periodo,
            Map<Long, BigDecimal> custoPorProjeto,
            Map<Long, Map<LocalDate, BigDecimal>> custoDiarioPorProjeto,
            Map<Long, List<AlertaIndicadorFinanceiroDTO>> alertasPorProjeto) {

        BigDecimal valorContratado = valorOuZero(projeto.getValorContratado());
        BigDecimal custo = valorOuZero(custoPorProjeto.get(projeto.getId()));

        IndicadorFinanceiroProjetoDTO dto = new IndicadorFinanceiroProjetoDTO();
        dto.setProjetoId(projeto.getId());
        dto.setNome(projeto.getNome());
        dto.setPeriodoInicio(periodo.inicio());
        dto.setPeriodoFim(periodo.fim());
        dto.setPeriodicidade(periodicidade);
        dto.setValorContratado(valorContratado);
        dto.setCustoRealAcumulado(custo);
        dto.setFaturamentoPrevisto(custo);
        dto.setPercentualConsumido(calcularPercentual(custo, valorContratado));
        dto.setStatusFinanceiro(calcularStatus(dto.getPercentualConsumido()));
        // Ainda nao existe historico salarial; o indicador declara que usa o custo atual do usuario.
        dto.setBaseCalculoCusto("VALOR_ATUAL");
        dto.setAlertas(alertasPorProjeto.getOrDefault(projeto.getId(), List.of()));
        dto.setEvolucao(montarEvolucao(periodo, valorContratado, custoDiarioPorProjeto.get(projeto.getId())));
        return dto;
    }

    private Map<Long, List<AlertaIndicadorFinanceiroDTO>> calcularAlertasPorProjeto(
            List<HorasAprovadasAgregadoDTO> horas,
            Map<Long, UsuarioFinanceiroDTO> usuarios) {
        Map<Long, List<AlertaIndicadorFinanceiroDTO>> alertas = new HashMap<>();
        Set<String> alertasEmitidos = new HashSet<>();

        for (HorasAprovadasAgregadoDTO item : horas) {
            UsuarioFinanceiroDTO usuario = usuarios.get(item.getUsuarioId());
            String codigo = obterCodigoAlertaConfiguracaoFinanceira(usuario);
            if (codigo == null) {
                continue;
            }

            String chave = item.getProjetoId() + ":" + item.getUsuarioId() + ":" + codigo;
            if (alertasEmitidos.add(chave)) {
                alertas.computeIfAbsent(item.getProjetoId(), id -> new ArrayList<>())
                        .add(new AlertaIndicadorFinanceiroDTO(
                                item.getUsuarioId(),
                                codigo,
                                "Usuario com horas aprovadas sem configuracao financeira completa; custo calculado como zero."));
            }
        }

        return alertas;
    }

    private String obterCodigoAlertaConfiguracaoFinanceira(UsuarioFinanceiroDTO usuario) {
        if (usuario == null) {
            return "USUARIO_FINANCEIRO_NAO_ENCONTRADO";
        }
        if (usuario.getTipoContratacao() == null) {
            return "TIPO_CONTRATACAO_NAO_CONFIGURADO";
        }
        if ((usuario.getTipoContratacao() == TipoContratacao.CLT || usuario.getTipoContratacao() == TipoContratacao.PJ_HORAS_FIXAS)
                && valorOuZero(usuario.getValorMensal()).compareTo(BigDecimal.ZERO) <= 0) {
            return "VALOR_MENSAL_NAO_CONFIGURADO";
        }
        if (usuario.getTipoContratacao() == TipoContratacao.PJ_HORAS_VARIAVEIS
                && valorOuZero(usuario.getValorHora()).compareTo(BigDecimal.ZERO) <= 0) {
            return "VALOR_HORA_NAO_CONFIGURADO";
        }
        return null;
    }

    private List<EvolucaoFinanceiraProjetoDTO> montarEvolucao(Periodo periodo, BigDecimal valorContratado, Map<LocalDate, BigDecimal> custosDiarios) {
        List<EvolucaoFinanceiraProjetoDTO> evolucao = new ArrayList<>();
        BigDecimal acumulado = ZERO;
        Map<LocalDate, BigDecimal> custos = custosDiarios == null ? Map.of() : custosDiarios;

        for (LocalDate data = periodo.inicio(); !data.isAfter(periodo.fim()); data = data.plusDays(1)) {
            acumulado = acumulado.add(valorOuZero(custos.get(data))).setScale(2, RoundingMode.HALF_UP);
            evolucao.add(new EvolucaoFinanceiraProjetoDTO(data, acumulado, valorContratado));
        }

        return evolucao;
    }

    private Map<Long, BigDecimal> calcularCustoPorProjeto(List<HorasAprovadasAgregadoDTO> horas, Map<Long, UsuarioFinanceiroDTO> usuarios) {
        Map<Long, BigDecimal> resultado = new HashMap<>();
        for (HorasAprovadasAgregadoDTO item : horas) {
            BigDecimal custo = valorOuZero(item.getHorasAprovadas()).multiply(calcularCustoHora(usuarios.get(item.getUsuarioId())));
            resultado.merge(item.getProjetoId(), custo, BigDecimal::add);
        }
        return normalizarValores(resultado);
    }

    private Map<Long, Map<LocalDate, BigDecimal>> calcularCustoDiarioPorProjeto(List<HorasAprovadasEvolucaoDTO> horas, Map<Long, UsuarioFinanceiroDTO> usuarios) {
        Map<Long, Map<LocalDate, BigDecimal>> resultado = new HashMap<>();
        for (HorasAprovadasEvolucaoDTO item : horas) {
            BigDecimal custo = valorOuZero(item.getHorasAprovadas()).multiply(calcularCustoHora(usuarios.get(item.getUsuarioId())));
            resultado
                    .computeIfAbsent(item.getProjetoId(), id -> new HashMap<>())
                    .merge(item.getDataLancamento(), custo, BigDecimal::add);
        }
        resultado.values().forEach(this::normalizarValores);
        return resultado;
    }

    private <K> Map<K, BigDecimal> normalizarValores(Map<K, BigDecimal> valores) {
        valores.replaceAll((chave, valor) -> valorOuZero(valor));
        return valores;
    }

    private BigDecimal calcularCustoHora(UsuarioFinanceiroDTO usuario) {
        if (usuario == null || usuario.getTipoContratacao() == null) {
            return ZERO;
        }

        TipoContratacao tipo = usuario.getTipoContratacao();
        if (tipo == TipoContratacao.CLT) {
            return dividir(valorOuZero(usuario.getValorMensal()), HORAS_CLT_MES);
        }
        if (tipo == TipoContratacao.PJ_HORAS_FIXAS) {
            return dividir(valorOuZero(usuario.getValorMensal()), HORAS_PJ_FIXAS_MES);
        }
        if (tipo == TipoContratacao.PJ_HORAS_VARIAVEIS) {
            return valorOuZero(usuario.getValorHora());
        }
        return ZERO;
    }

    private BigDecimal calcularPercentual(BigDecimal custo, BigDecimal valorContratado) {
        if (valorContratado == null || valorContratado.compareTo(BigDecimal.ZERO) <= 0) {
            return ZERO;
        }
        return custo.multiply(CEM).divide(valorContratado, 2, RoundingMode.HALF_UP);
    }

    private StatusFinanceiroProjeto calcularStatus(BigDecimal percentual) {
        if (percentual.compareTo(CEM) >= 0) {
            return StatusFinanceiroProjeto.CRITICAL;
        }
        if (percentual.compareTo(BigDecimal.valueOf(80)) >= 0) {
            return StatusFinanceiroProjeto.WARNING;
        }
        return StatusFinanceiroProjeto.OK;
    }

    private Periodo resolverPeriodo(PeriodicidadeFinanceira periodicidade, Integer ano, Integer mes, LocalDate dataReferencia) {
        PeriodicidadeFinanceira tipo = periodicidade == null ? PeriodicidadeFinanceira.MENSAL : periodicidade;
        LocalDate referencia = dataReferencia == null ? LocalDate.now(ZONE_ID) : dataReferencia;

        // O fechamento usa a data de lancamento das horas, nao a data de criacao do apontamento.
        if (tipo == PeriodicidadeFinanceira.SEMANAL) {
            LocalDate inicio = referencia.with(DayOfWeek.MONDAY);
            return new Periodo(inicio, inicio.plusDays(6));
        }

        int anoBase = ano == null ? referencia.getYear() : ano;
        int mesBase = mes == null ? referencia.getMonthValue() : mes;
        YearMonth yearMonth = YearMonth.of(anoBase, mesBase);
        return new Periodo(yearMonth.atDay(1), yearMonth.atEndOfMonth());
    }

    private List<HorasAprovadasAgregadoDTO> buscarHorasAgregadas(Periodo periodo) {
        String url = apontamentoHorasUrl + "/horas/financeiro/aprovadas?dataInicio={dataInicio}&dataFim={dataFim}";
        return getList(url, new ParameterizedTypeReference<List<HorasAprovadasAgregadoDTO>>() {}, periodo.inicio(), periodo.fim());
    }

    private List<HorasAprovadasEvolucaoDTO> buscarHorasEvolucao(Periodo periodo) {
        String url = apontamentoHorasUrl + "/horas/financeiro/aprovadas/evolucao?dataInicio={dataInicio}&dataFim={dataFim}";
        return getList(url, new ParameterizedTypeReference<List<HorasAprovadasEvolucaoDTO>>() {}, periodo.inicio(), periodo.fim());
    }

    private Map<Long, UsuarioFinanceiroDTO> buscarUsuariosFinanceiros(List<HorasAprovadasAgregadoDTO> agregadas, List<HorasAprovadasEvolucaoDTO> evolucao) {
        Set<Long> ids = new HashSet<>();
        agregadas.forEach(item -> ids.add(item.getUsuarioId()));
        evolucao.forEach(item -> ids.add(item.getUsuarioId()));
        ids.remove(null);

        if (ids.isEmpty()) {
            return Map.of();
        }

        String queryIds = ids.stream().map(String::valueOf).collect(Collectors.joining(","));
        String url = usuarioUrl + "/usuario/financeiro?ids=" + queryIds;
        List<UsuarioFinanceiroDTO> usuarios = getListInterno(url, new ParameterizedTypeReference<List<UsuarioFinanceiroDTO>>() {});
        return usuarios.stream().collect(Collectors.toMap(UsuarioFinanceiroDTO::getId, Function.identity()));
    }

    private <T> List<T> getList(String url, ParameterizedTypeReference<List<T>> type, Object... params) {
        try {
            List<T> body = restTemplate.exchange(url, HttpMethod.GET, null, type, params).getBody();
            return body == null ? List.of() : body;
        } catch (RestClientException ex) {
            throw new RuntimeException("Falha ao consultar servico externo para indicadores financeiros: " + url, ex);
        }
    }

    private <T> List<T> getListInterno(String url, ParameterizedTypeReference<List<T>> type) {
        if (internalApiKey == null || internalApiKey.isBlank()) {
            throw new RuntimeException("Chave interna nao configurada para consultar dados financeiros de usuarios.");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Internal-Api-Key", internalApiKey);

        try {
            List<T> body = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headers), type).getBody();
            return body == null ? List.of() : body;
        } catch (RestClientException ex) {
            throw new RuntimeException("Falha ao consultar servico interno de usuarios para indicadores financeiros: " + url, ex);
        }
    }

    private RestTemplate criarRestTemplateComTimeout() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(5000);
        factory.setReadTimeout(5000);
        return new RestTemplate(factory);
    }

    private BigDecimal dividir(BigDecimal valor, BigDecimal divisor) {
        if (valor.compareTo(BigDecimal.ZERO) == 0) {
            return ZERO;
        }
        return valor.divide(divisor, 6, RoundingMode.HALF_UP);
    }

    private BigDecimal valorOuZero(BigDecimal valor) {
        return valor == null ? ZERO : valor.setScale(2, RoundingMode.HALF_UP);
    }

    private record Periodo(LocalDate inicio, LocalDate fim) {
    }
}
