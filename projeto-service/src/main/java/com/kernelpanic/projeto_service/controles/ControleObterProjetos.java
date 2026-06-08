package com.kernelpanic.projeto_service.controles;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kernelpanic.projeto_service.dtos.ProjetoExibirDTO;
import com.kernelpanic.projeto_service.servicos.ProjetoServico;

@RestController
@RequestMapping("/projeto")
public class ControleObterProjetos {

    @Autowired
    private ProjetoServico servico;

    @GetMapping
    public List<ProjetoExibirDTO> obterTodosProjetos() {
        return servico.obterTodos();
    }

    @GetMapping("/profissional/{profissionalId}")
    public List<ProjetoExibirDTO> obterProjetosPorProfissional(
            @PathVariable Long profissionalId,
            @RequestParam(required = false) String dataInicio,
            @RequestParam(required = false) String dataFim,
            @RequestParam(required = false) Long projetoId,
            @RequestParam(required = false) String status) {
        
        List<ProjetoExibirDTO> projetos;
        if (dataInicio != null && dataFim != null) {
            LocalDateTime inicio = LocalDateTime.parse(dataInicio + "T00:00:00");
            LocalDateTime fim = LocalDateTime.parse(dataFim + "T23:59:59");
            projetos = servico.obterProjetosPorProfissionalComPeriodo(profissionalId, inicio, fim);
        } else {
            projetos = servico.obterProjetosPorProfissional(profissionalId);
        }

        if (projetoId != null) {
            projetos = projetos.stream()
                    .filter(p -> projetoId.equals(p.getId()))
                    .toList();
        }

        if (status != null && !status.isBlank() && !"todos".equalsIgnoreCase(status)) {
            projetos = projetos.stream()
                    .filter(p -> status.equalsIgnoreCase(p.getStatus()))
                    .toList();
        }

        return projetos;
    }
}
