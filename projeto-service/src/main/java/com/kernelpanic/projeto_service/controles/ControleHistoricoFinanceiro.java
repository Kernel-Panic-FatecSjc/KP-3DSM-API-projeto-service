package com.kernelpanic.projeto_service.controles;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kernelpanic.projeto_service.dtos.AuditoriaFinanceiraDTO;
import com.kernelpanic.projeto_service.servicos.AuditoriaFinanceiraServico;

import lombok.RequiredArgsConstructor;

/**
 * Histórico de alterações com impacto financeiro (auditoria), opcionalmente
 * filtrado por projeto. Alimenta a tabela do dashboard financeiro.
 */
@RestController
@RequestMapping("/projeto")
@RequiredArgsConstructor
public class ControleHistoricoFinanceiro {

    private final AuditoriaFinanceiraServico servico;

    @GetMapping("/historico-financeiro")
    public List<AuditoriaFinanceiraDTO> historico(@RequestParam(required = false) Long projetoId) {
        return servico.listar(projetoId);
    }
}
