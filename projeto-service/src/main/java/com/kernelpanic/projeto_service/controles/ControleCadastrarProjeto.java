package com.kernelpanic.projeto_service.controles;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kernelpanic.projeto_service.dtos.ProjetoCadastrarDTO;
import com.kernelpanic.projeto_service.servicos.ProjetoServico;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/projeto")
public class ControleCadastrarProjeto {

    @Autowired
    private ProjetoServico servico;

    @PostMapping("/cadastro")
    public void cadastrarProjeto(@Valid @RequestBody ProjetoCadastrarDTO dto) {
        servico.cadastrarViaDTO(dto);
    }
}
