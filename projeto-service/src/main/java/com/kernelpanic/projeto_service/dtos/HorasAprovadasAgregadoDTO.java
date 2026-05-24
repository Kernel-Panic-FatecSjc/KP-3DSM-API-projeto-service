package com.kernelpanic.projeto_service.dtos;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class HorasAprovadasAgregadoDTO {
    private Long projetoId;
    private Long usuarioId;
    private BigDecimal horasAprovadas;
}
