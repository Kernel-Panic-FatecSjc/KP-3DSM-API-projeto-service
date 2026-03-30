package com.kernelpanic.projeto_service.excecoes;

public class EntidadeNaoEncontradaException extends RuntimeException {
    
    private String titulo;
    private String descricao;
    
    public EntidadeNaoEncontradaException(String titulo, String descricao) {
        super(descricao);
        this.titulo = titulo;
        this.descricao = descricao;
    }
    
    public String getTitulo() {
        return titulo;
    }
    
    public String getDescricao() {
        return descricao;
    }
}
