package com.kernelpanic.projeto_service.controles;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kernelpanic.projeto_service.dtos.ProjetoExibirDTO;
import com.kernelpanic.projeto_service.servicos.ProjetoServico;

@RestController
@RequestMapping("/projeto")
public class ControleObterProjetoPorId {

    @Autowired
    private ProjetoServico servico;

    @GetMapping("/{id}")
    public ProjetoExibirDTO obterProjetoPorId(@PathVariable Long id) {
        return servico.obterPorId(id);
    }
}
