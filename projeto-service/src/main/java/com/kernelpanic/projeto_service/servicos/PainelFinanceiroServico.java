package com.kernelpanic.projeto_service.servicos;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.kernelpanic.projeto_service.dtos.ClienteFinanceiroDTO;
import com.kernelpanic.projeto_service.dtos.HorasAprovadasAgregadoDTO;
import com.kernelpanic.projeto_service.dtos.PainelFinanceiroClienteDTO;
import com.kernelpanic.projeto_service.dtos.PainelFinanceiroDTO;
import com.kernelpanic.projeto_service.dtos.PainelFinanceiroProjetoDTO;
import com.kernelpanic.projeto_service.dtos.PainelFinanceiroResumoDTO;
import com.kernelpanic.projeto_service.dtos.UsuarioFinanceiroDTO;
import com.kernelpanic.projeto_service.enums.PeriodicidadeFinanceira;
import com.kernelpanic.projeto_service.repositorios.ProjetoFinanceiroProjection;
import com.kernelpanic.projeto_service.repositorios.ProjetoRepositorio;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PainelFinanceiroServico {

    private static final ZoneId ZONE_ID = ZoneId.of("America/Sao_Paulo");
    private static final BigDecimal ZERO = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
    private static final BigDecimal CEM = BigDecimal.valueOf(100);

    private final ProjetoRepositorio projetoRepositorio;
    private final IntegracaoFinanceiraCliente integracao;
    private final IntegracaoClienteServico integracaoCliente;

    public PainelFinanceiroDTO obterPainel(PeriodicidadeFinanceira periodicidade, Integer ano, Integer mes, LocalDate dataReferencia) {
        Periodo periodo = resolverPeriodo(periodicidade, ano, mes, dataReferencia);
        PeriodicidadeFinanceira tipo = periodicidade == null ? PeriodicidadeFinanceira.MENSAL : periodicidade;

        List<ProjetoFinanceiroProjection> projetos = projetoRepositorio.buscarProjetosParaPainelFinanceiro();
        Map<Long, BigDecimal> custoPorProjeto = calcularCustoPorProjeto(periodo);
        Map<Long, ClienteFinanceiroDTO> clientePorProjeto = buscarClientesPorProjeto(projetos);

        Map<ClienteChave, PainelFinanceiroClienteDTO> clientes = new HashMap<>();
        for (ProjetoFinanceiroProjection projeto : projetos) {
            ClienteChave chave = ClienteChave.of(clientePorProjeto.get(projeto.getId()));
            PainelFinanceiroClienteDTO cliente = clientes.computeIfAbsent(chave, this::novoCliente);
            adicionarProjeto(cliente, projeto, custoPorProjeto.get(projeto.getId()));
        }

        List<PainelFinanceiroClienteDTO> detalhamento = clientes.values().stream()
                .peek(this::finalizarCliente)
                .sorted(Comparator.comparing(PainelFinanceiroClienteDTO::getNome, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)))
                .toList();

        PainelFinanceiroDTO dto = new PainelFinanceiroDTO();
        dto.setPeriodoInicio(periodo.inicio());
        dto.setPeriodoFim(periodo.fim());
        dto.setPeriodicidade(tipo);
        dto.setResumo(montarResumo(detalhamento));
        dto.setClientes(detalhamento);
        return dto;
    }

    private Map<Long, ClienteFinanceiroDTO> buscarClientesPorProjeto(List<ProjetoFinanceiroProjection> projetos) {
        Set<Long> projetoIds = projetos.stream()
                .map(ProjetoFinanceiroProjection::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        List<ClienteFinanceiroDTO> clientes = integracaoCliente.buscarClientesPorProjetos(projetoIds);

        Map<Long, ClienteFinanceiroDTO> clientePorProjeto = new HashMap<>();
        for (ClienteFinanceiroDTO cliente : clientes) {
            if (cliente.getProjetoIds() == null) {
                continue;
            }
            for (Long projetoId : cliente.getProjetoIds()) {
                ClienteFinanceiroDTO clienteAnterior = clientePorProjeto.putIfAbsent(projetoId, cliente);
                if (clienteAnterior != null && !Objects.equals(clienteAnterior.getId(), cliente.getId())) {
                    throw new IllegalStateException("Projeto " + projetoId
                            + " possui vinculo com mais de um cliente: "
                            + clienteAnterior.getId() + " e " + cliente.getId() + ".");
                }
            }
        }
        return clientePorProjeto;
    }

    private Map<Long, BigDecimal> calcularCustoPorProjeto(Periodo periodo) {
        List<HorasAprovadasAgregadoDTO> horas = integracao.buscarHorasAgregadas(periodo.inicio(), periodo.fim());
        Set<Long> usuarioIds = horas.stream()
                .map(HorasAprovadasAgregadoDTO::getUsuarioId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        Map<Long, UsuarioFinanceiroDTO> usuarios = integracao.buscarUsuariosFinanceiros(usuarioIds);

        Map<Long, BigDecimal> resultado = new HashMap<>();
        for (HorasAprovadasAgregadoDTO item : horas) {
            BigDecimal custo = integracao.valorOuZero(item.getHorasAprovadas())
                    .multiply(integracao.calcularCustoHora(usuarios.get(item.getUsuarioId())));
            resultado.merge(item.getProjetoId(), custo, BigDecimal::add);
        }
        resultado.replaceAll((id, valor) -> valorOuZero(valor));
        return resultado;
    }

    private PainelFinanceiroClienteDTO novoCliente(ClienteChave chave) {
        PainelFinanceiroClienteDTO dto = new PainelFinanceiroClienteDTO();
        dto.setClienteId(chave.id());
        dto.setNome(chave.nome());
        dto.setStatus(chave.status());
        dto.setProjetosAtivos(0L);
        dto.setValorContratado(ZERO);
        dto.setCustoReal(ZERO);
        dto.setLucro(ZERO);
        dto.setMargem(ZERO);
        return dto;
    }

    private void adicionarProjeto(PainelFinanceiroClienteDTO cliente, ProjetoFinanceiroProjection projeto, BigDecimal custoRealProjeto) {
        BigDecimal valorContratado = valorOuZero(projeto.getValorContratado());
        BigDecimal custoReal = valorOuZero(custoRealProjeto);
        BigDecimal lucro = valorContratado.subtract(custoReal).setScale(2, RoundingMode.HALF_UP);

        cliente.setValorContratado(cliente.getValorContratado().add(valorContratado).setScale(2, RoundingMode.HALF_UP));
        cliente.setCustoReal(cliente.getCustoReal().add(custoReal).setScale(2, RoundingMode.HALF_UP));
        cliente.setLucro(cliente.getLucro().add(lucro).setScale(2, RoundingMode.HALF_UP));
        if (isProjetoAtivo(projeto)) {
            cliente.setProjetosAtivos(cliente.getProjetosAtivos() + 1);
        }

        cliente.getProjetosVinculados().add(new PainelFinanceiroProjetoDTO(
                projeto.getId(),
                projeto.getNome(),
                projeto.getStatus(),
                valorContratado,
                custoReal,
                lucro));
    }

    private void finalizarCliente(PainelFinanceiroClienteDTO cliente) {
        cliente.setMargem(calcularMargem(cliente.getLucro(), cliente.getValorContratado()));
        cliente.getProjetosVinculados().sort(Comparator.comparing(PainelFinanceiroProjetoDTO::getNome, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)));
    }

    private PainelFinanceiroResumoDTO montarResumo(List<PainelFinanceiroClienteDTO> clientes) {
        BigDecimal valorContratadoTotal = clientes.stream()
                .map(PainelFinanceiroClienteDTO::getValorContratado)
                .reduce(ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
        BigDecimal lucroTotal = clientes.stream()
                .map(PainelFinanceiroClienteDTO::getLucro)
                .reduce(ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
        long projetosAtivos = clientes.stream()
                .mapToLong(PainelFinanceiroClienteDTO::getProjetosAtivos)
                .sum();

        long clientesCadastrados = clientes.stream()
                .filter(cliente -> cliente.getClienteId() != null)
                .count();

        return new PainelFinanceiroResumoDTO(clientesCadastrados, projetosAtivos, valorContratadoTotal, lucroTotal);
    }

    private Periodo resolverPeriodo(PeriodicidadeFinanceira periodicidade, Integer ano, Integer mes, LocalDate dataReferencia) {
        PeriodicidadeFinanceira tipo = periodicidade == null ? PeriodicidadeFinanceira.MENSAL : periodicidade;
        LocalDate referencia = dataReferencia == null ? LocalDate.now(ZONE_ID) : dataReferencia;

        if (ano != null && ano < 0) {
            throw new IllegalArgumentException("O parametro ano nao pode ser negativo.");
        }
        if (mes != null && (mes < 1 || mes > 12)) {
            throw new IllegalArgumentException("O parametro mes deve estar entre 1 e 12.");
        }

        if (tipo == PeriodicidadeFinanceira.SEMANAL) {
            LocalDate inicio = referencia.with(DayOfWeek.MONDAY);
            return new Periodo(inicio, inicio.plusDays(6));
        }

        int anoBase = ano == null ? referencia.getYear() : ano;
        int mesBase = mes == null ? referencia.getMonthValue() : mes;
        YearMonth yearMonth = YearMonth.of(anoBase, mesBase);
        return new Periodo(yearMonth.atDay(1), yearMonth.atEndOfMonth());
    }

    private boolean isProjetoAtivo(ProjetoFinanceiroProjection projeto) {
        String status = projeto.getStatus();
        return status != null
                && !"CONCLUIDO".equalsIgnoreCase(status)
                && !"CANCELADO".equalsIgnoreCase(status)
                && !"FINALIZADO".equalsIgnoreCase(status);
    }

    private BigDecimal calcularMargem(BigDecimal lucro, BigDecimal valorContratado) {
        if (valorContratado == null || valorContratado.compareTo(BigDecimal.ZERO) <= 0) {
            return ZERO;
        }
        return valorOuZero(lucro).multiply(CEM).divide(valorContratado, 2, RoundingMode.HALF_UP);
    }

    private BigDecimal valorOuZero(BigDecimal valor) {
        return valor == null ? ZERO : valor.setScale(2, RoundingMode.HALF_UP);
    }

    private record Periodo(LocalDate inicio, LocalDate fim) {
    }

    private record ClienteChave(Long id, String nome, String status) {
        static ClienteChave of(ClienteFinanceiroDTO cliente) {
            if (cliente == null) {
                return new ClienteChave(null, "Cliente nao vinculado", "NAO_VINCULADO");
            }
            String nome = textoOuPadrao(cliente.getNome(), "Cliente " + cliente.getId());
            String status = Boolean.TRUE.equals(cliente.getAtivo()) ? "ATIVO" : "INATIVO";
            return new ClienteChave(cliente.getId(), nome, status);
        }

        private static String textoOuPadrao(String valor, String padrao) {
            return valor == null || valor.isBlank() ? padrao : valor;
        }
    }
}
