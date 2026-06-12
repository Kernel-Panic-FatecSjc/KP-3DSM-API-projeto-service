package com.kernelpanic.projeto_service.dtos;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.kernelpanic.projeto_service.enums.PeriodicidadeFinanceira;

import lombok.Data;

@Data
public class PainelFinanceiroDTO {
    private LocalDate periodoInicio;
    private LocalDate periodoFim;
    private PeriodicidadeFinanceira periodicidade;
    private PainelFinanceiroResumoDTO resumo;
    private List<PainelFinanceiroClienteDTO> clientes = new ArrayList<>();
}
