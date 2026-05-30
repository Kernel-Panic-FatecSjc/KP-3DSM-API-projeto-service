package com.kernelpanic.projeto_service.dtos;

import java.math.BigDecimal;

import com.kernelpanic.projeto_service.enums.TipoContratacao;

import lombok.Data;

@Data
public class UsuarioFinanceiroDTO {
    private Long id;
    private String nome;
    private TipoContratacao tipoContratacao;
    private BigDecimal valorMensal;
    private BigDecimal valorHora;
}
