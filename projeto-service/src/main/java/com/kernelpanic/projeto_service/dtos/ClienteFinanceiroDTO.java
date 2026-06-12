package com.kernelpanic.projeto_service.dtos;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class ClienteFinanceiroDTO {
    private Long id;
    private String nome;
    private String cnpj;
    private String email;
    private String telefone;
    private String observacao;
    private Boolean ativo;
    private List<Long> projetoIds = new ArrayList<>();
}
