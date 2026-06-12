package com.kernelpanic.projeto_service.dtos;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PainelFinanceiroResumoDTO {
    private Long clientesCadastrados;
    private Long projetosAtivos;
    private BigDecimal valorContratadoTotal;
    private BigDecimal lucroTotal;
}
