package com.kernelpanic.projeto_service.servicos;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.kernelpanic.projeto_service.dtos.ClienteFinanceiroDTO;

@Component
public class IntegracaoClienteServico {

    private final RestTemplate restTemplate = criarRestTemplate();

    @Value("${integracoes.usuario.url:http://localhost:8083}")
    private String usuarioUrl;

    public List<ClienteFinanceiroDTO> buscarClientesPorProjetos(Collection<Long> projetoIds) {
        if (projetoIds == null || projetoIds.isEmpty()) {
            return List.of();
        }

        String ids = projetoIds.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
        String url = usuarioUrl + "/clientes/projetos?ids=" + ids;

        try {
            List<ClienteFinanceiroDTO> body = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<ClienteFinanceiroDTO>>() {})
                    .getBody();
            return body == null ? List.of() : body;
        } catch (RestClientException ex) {
            throw new RuntimeException("Falha ao consultar clientes por projeto no usuario-service: " + url, ex);
        }
    }

    private static RestTemplate criarRestTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(5000);
        factory.setReadTimeout(5000);
        return new RestTemplate(factory);
    }
}
