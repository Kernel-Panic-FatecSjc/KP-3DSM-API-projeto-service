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

        if (atualizacao.getStatus() != null && !atualizacao.getStatus().isEmpty()) {
            projetoAtual.setStatus(atualizacao.getStatus());
        }

        if (atualizacao.getValorContratado() != null) {
            projetoAtual.setValorContratado(atualizacao.getValorContratado());
        }
        
        if (atualizacao.getPrazo() != null) {
            projetoAtual.setPrazo(atualizacao.getPrazo());
        }

        if (atualizacao.getValorContratado() != null) {
            projetoAtual.setValorContratado(atualizacao.getValorContratado());
        }

        if (atualizacao.getResponsavelId() != null) {
            projetoAtual.setResponsavelId(atualizacao.getResponsavelId());
        }

        if (atualizacao.getProfissionaisIds() != null) {
            projetoAtual.setProfissionaisIds(atualizacao.getProfissionaisIds());
        }
    }
}
