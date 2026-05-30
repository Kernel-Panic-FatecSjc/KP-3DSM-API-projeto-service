package com.kernelpanic.projeto_service.dtos;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Data;

@Data
public class HorasAprovadasEvolucaoDTO {
    private Long projetoId;
    private Long usuarioId;
    private LocalDate dataLancamento;
    private BigDecimal horasAprovadas;
}
