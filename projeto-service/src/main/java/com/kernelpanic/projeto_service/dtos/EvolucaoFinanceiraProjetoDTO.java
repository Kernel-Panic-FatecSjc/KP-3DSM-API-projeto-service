package com.kernelpanic.projeto_service.dtos;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EvolucaoFinanceiraProjetoDTO {
    private LocalDate data;
    private BigDecimal custoAcumulado;
    private BigDecimal valorContratado;
}
