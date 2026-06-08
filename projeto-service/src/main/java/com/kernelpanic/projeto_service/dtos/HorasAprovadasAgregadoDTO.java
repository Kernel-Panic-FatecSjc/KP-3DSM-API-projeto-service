package com.kernelpanic.projeto_service.dtos;

import java.math.BigDecimal;

import lombok.Data;
import lombok.AllArgsConstructor;

@Data
@AllArgsConstructor
public class HorasAprovadasAgregadoDTO {
    private Long projetoId;
    private Long usuarioId;
    private BigDecimal horasAprovadas;
}
