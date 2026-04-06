package com.kernelpanic.projeto_service.controles;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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
}
