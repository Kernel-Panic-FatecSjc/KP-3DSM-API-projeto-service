package com.kernelpanic.projeto_service.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AlertaIndicadorFinanceiroDTO {
    private Long usuarioId;
    private String codigo;
    private String mensagem;
}
