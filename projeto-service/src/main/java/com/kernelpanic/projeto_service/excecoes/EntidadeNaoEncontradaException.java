package com.kernelpanic.projeto_service.excecoes;

public class EntidadeNaoEncontradaException extends RuntimeException {
    private String descricao;

    public EntidadeNaoEncontradaException(String mensagem, String descricao) {
        super(mensagem);
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
