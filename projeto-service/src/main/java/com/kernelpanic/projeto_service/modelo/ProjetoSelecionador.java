package com.kernelpanic.projeto_service.modelo;

import org.springframework.stereotype.Component;

import com.kernelpanic.projeto_service.entidades.Projeto;

@Component
public class ProjetoSelecionador {
    
    public Projeto selecionar(java.util.List<Projeto> projetos, Long id) {
        return projetos.stream()
                .filter(projeto -> projeto.getId().equals(id))
                .findFirst()
                .orElse(null);
    }
}
