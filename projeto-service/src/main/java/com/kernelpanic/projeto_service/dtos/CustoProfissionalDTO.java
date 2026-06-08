package com.kernelpanic.projeto_service.dtos;

import java.math.BigDecimal;

import lombok.Data;

/**
 * Custo de um profissional em um projeto no período (horas aprovadas × custo/hora).
 * Uma linha por (profissional, projeto); o frontend pivota para o gráfico empilhado.
 */
@Data
public class CustoProfissionalDTO {
    private Long usuarioId;
    private String usuarioNome;
    private Long projetoId;
    private String projetoNome;
    private BigDecimal horasAprovadas;
    private BigDecimal custoTotal;
}
