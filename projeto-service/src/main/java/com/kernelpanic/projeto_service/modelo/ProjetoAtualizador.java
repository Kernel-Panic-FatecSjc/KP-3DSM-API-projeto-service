package com.kernelpanic.projeto_service.modelo;

import org.springframework.stereotype.Component;

import com.kernelpanic.projeto_service.entidades.Projeto;

@Component
public class ProjetoAtualizador {
    
    public void atualizar(Projeto projetoAtual, Projeto atualizacao) {
        if (atualizacao.getNome() != null && !atualizacao.getNome().isEmpty()) {
            projetoAtual.setNome(atualizacao.getNome());
        }
        
        if (atualizacao.getDescricao() != null && !atualizacao.getDescricao().isEmpty()) {
            projetoAtual.setDescricao(atualizacao.getDescricao());
        }
        
        if (atualizacao.getPrazo() != null) {
            projetoAtual.setPrazo(atualizacao.getPrazo());
        }

        if (atualizacao.getValorContratado() != null) {
            projetoAtual.setValorContratado(atualizacao.getValorContratado());
        }
    }
}
