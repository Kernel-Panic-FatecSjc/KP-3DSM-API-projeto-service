package com.kernelpanic.projeto_service.dtos;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class PainelFinanceiroClienteDTO {
    private Long clienteId;
    private String nome;
    private String status;
    private Long projetosAtivos;
    private BigDecimal valorContratado;
    private BigDecimal custoReal;
    private BigDecimal lucro;
    private BigDecimal margem;
    private List<PainelFinanceiroProjetoDTO> projetosVinculados = new ArrayList<>();
}
