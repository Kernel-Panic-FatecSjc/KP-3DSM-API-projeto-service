package com.kernelpanic.projeto_service.dtos;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Item do histórico de alterações com impacto financeiro (dashboard financeiro).
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuditoriaFinanceiraDTO {
    private Long id;
    private LocalDateTime data;
    private Long projetoId;
    private String projetoNome;
    private String tipo;
    private String descricao;
    private BigDecimal impacto;
}
