package com.kernelpanic.projeto_service.controles;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kernelpanic.projeto_service.servicos.ProjetoServico;

@RestController
@RequestMapping("/projeto")
public class ControleDeletarProjeto {

    @Autowired
    private ProjetoServico servico;

    @DeleteMapping("/{id}")
    public void deletarProjeto(@PathVariable Long id) {
        servico.deletarPorId(id);
    }
}
