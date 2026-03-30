package com.kernelpanic.projeto_service.controles;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kernelpanic.projeto_service.dtos.ProjetoAtualizarDTO;
import com.kernelpanic.projeto_service.servicos.ProjetoServico;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/projeto")
public class ControleAtualizarProjeto {

    @Autowired
    private ProjetoServico servico;

    @PutMapping("/{id}/atualizacao")
    public void atualizarProjeto(@PathVariable Long id, @Valid @RequestBody ProjetoAtualizarDTO dto) {
        servico.atualizarViaDTO(id, dto);
    }
}
