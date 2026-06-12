package com.kernelpanic.projeto_service.dtos;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PainelFinanceiroProjetoDTO {
    private Long id;
    private String nome;
    private String status;
    private BigDecimal valorContratado;
    private BigDecimal custoReal;
    private BigDecimal lucro;
}
