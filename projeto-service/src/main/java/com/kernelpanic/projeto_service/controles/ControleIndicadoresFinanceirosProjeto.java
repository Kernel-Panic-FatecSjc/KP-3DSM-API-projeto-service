package com.kernelpanic.projeto_service.controles;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kernelpanic.projeto_service.dtos.IndicadorFinanceiroProjetoDTO;
import com.kernelpanic.projeto_service.enums.PeriodicidadeFinanceira;
import com.kernelpanic.projeto_service.servicos.IndicadorFinanceiroProjetoServico;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/projeto")
@RequiredArgsConstructor
public class ControleIndicadoresFinanceirosProjeto {

    private final IndicadorFinanceiroProjetoServico servico;

    @GetMapping("/indicadores-financeiros")
    public List<IndicadorFinanceiroProjetoDTO> obterIndicadoresFinanceiros(
            @RequestParam(defaultValue = "MENSAL") PeriodicidadeFinanceira periodicidade,
            @RequestParam(required = false) Integer ano,
            @RequestParam(required = false) Integer mes,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataReferencia) {
        return servico.obterIndicadores(periodicidade, ano, mes, dataReferencia);
    }
}
