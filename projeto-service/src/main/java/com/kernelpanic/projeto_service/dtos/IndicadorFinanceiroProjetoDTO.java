package com.kernelpanic.projeto_service.dtos;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.kernelpanic.projeto_service.enums.PeriodicidadeFinanceira;
import com.kernelpanic.projeto_service.enums.StatusFinanceiroProjeto;

import lombok.Data;

@Data
public class IndicadorFinanceiroProjetoDTO {
    private Long projetoId;
    private String nome;
    private LocalDate periodoInicio;
    private LocalDate periodoFim;
    private PeriodicidadeFinanceira periodicidade;
    private BigDecimal valorContratado;
    private BigDecimal custoRealAcumulado;
    private BigDecimal percentualConsumido;
    private StatusFinanceiroProjeto statusFinanceiro;
    private BigDecimal faturamentoPrevisto;
    private String baseCalculoCusto;
    private List<AlertaIndicadorFinanceiroDTO> alertas = new ArrayList<>();
    private List<EvolucaoFinanceiraProjetoDTO> evolucao = new ArrayList<>();
}
