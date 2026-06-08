package com.kernelpanic.projeto_service.servicos;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.kernelpanic.projeto_service.dtos.HorasAprovadasAgregadoDTO;
import com.kernelpanic.projeto_service.dtos.UsuarioFinanceiroDTO;
import com.kernelpanic.projeto_service.enums.TipoContratacao;

/**
 * Cliente das integrações financeiras (apontamento-horas e usuario) e regras de
 * custo/hora. Espelha o contrato usado em {@link IndicadorFinanceiroProjetoServico}
 * para que novos cálculos (ex.: custo por profissional) reaproveitem a mesma base
 * sem duplicar a chamada protegida por X-Internal-Api-Key.
 */
@Component
public class IntegracaoFinanceiraCliente {

    private static final BigDecimal HORAS_CLT_MES = BigDecimal.valueOf(220);
    private static final BigDecimal HORAS_PJ_FIXAS_MES = BigDecimal.valueOf(160);
    private static final BigDecimal ZERO = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);

    private final RestTemplate restTemplate = criarRestTemplate();

    @Value("${integracoes.apontamento-horas.url:http://localhost:8084}")
    private String apontamentoHorasUrl;

    @Value("${integracoes.usuario.url:http://localhost:8083}")
    private String usuarioUrl;

    @Value("${internal.api-key:}")
    private String internalApiKey;

    /** Horas aprovadas agregadas por (projeto, usuário) no período. */
    public List<HorasAprovadasAgregadoDTO> buscarHorasAgregadas(LocalDate inicio, LocalDate fim) {
        String url = apontamentoHorasUrl + "/horas/financeiro/aprovadas?dataInicio={dataInicio}&dataFim={dataFim}";
        try {
            List<HorasAprovadasAgregadoDTO> body = restTemplate.exchange(
                    url, HttpMethod.GET, null,
                    new ParameterizedTypeReference<List<HorasAprovadasAgregadoDTO>>() {},
                    inicio, fim).getBody();
            return body == null ? List.of() : body;
        } catch (RestClientException ex) {
            throw new RuntimeException("Falha ao consultar horas aprovadas: " + url, ex);
        }
    }

    /** Dados financeiros (salário/valor-hora) dos usuários, por id. Exige a chave interna. */
    public Map<Long, UsuarioFinanceiroDTO> buscarUsuariosFinanceiros(Collection<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return Map.of();
        }
        if (internalApiKey == null || internalApiKey.isBlank()) {
            throw new RuntimeException("Chave interna não configurada para consultar dados financeiros de usuários.");
        }

        String queryIds = ids.stream().filter(Objects::nonNull).map(String::valueOf).collect(Collectors.joining(","));
        String url = usuarioUrl + "/usuario/financeiro?ids=" + queryIds;

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Internal-Api-Key", internalApiKey);

        try {
            List<UsuarioFinanceiroDTO> body = restTemplate.exchange(
                    url, HttpMethod.GET, new HttpEntity<>(headers),
                    new ParameterizedTypeReference<List<UsuarioFinanceiroDTO>>() {}).getBody();
            if (body == null) {
                return Map.of();
            }
            return body.stream().collect(Collectors.toMap(UsuarioFinanceiroDTO::getId, Function.identity()));
        } catch (RestClientException ex) {
            throw new RuntimeException("Falha ao consultar usuários financeiros: " + url, ex);
        }
    }

    /** Custo/hora conforme o tipo de contratação: CLT ÷220h, PJ fixo ÷160h, PJ variável = valor/hora. */
    public BigDecimal calcularCustoHora(UsuarioFinanceiroDTO usuario) {
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

    public BigDecimal valorOuZero(BigDecimal valor) {
        return valor == null ? ZERO : valor.setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal dividir(BigDecimal valor, BigDecimal divisor) {
        if (valor.compareTo(BigDecimal.ZERO) == 0) {
            return ZERO;
        }
        return valor.divide(divisor, 6, RoundingMode.HALF_UP);
    }

    private static RestTemplate criarRestTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(5000);
        factory.setReadTimeout(5000);
        return new RestTemplate(factory);
    }
}
