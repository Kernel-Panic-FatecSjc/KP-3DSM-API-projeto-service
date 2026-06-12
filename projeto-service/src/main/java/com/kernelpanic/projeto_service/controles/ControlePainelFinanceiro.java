package com.kernelpanic.projeto_service.controles;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kernelpanic.projeto_service.dtos.PainelFinanceiroDTO;
import com.kernelpanic.projeto_service.enums.PeriodicidadeFinanceira;
import com.kernelpanic.projeto_service.servicos.PainelFinanceiroServico;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/projeto")
@RequiredArgsConstructor
public class ControlePainelFinanceiro {

    private final PainelFinanceiroServico servico;

    @GetMapping("/painel-financeiro")
    public PainelFinanceiroDTO obterPainelFinanceiro(
            @RequestParam(defaultValue = "MENSAL") PeriodicidadeFinanceira periodicidade,
            @RequestParam(required = false) Integer ano,
            @RequestParam(required = false) Integer mes,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataReferencia) {
        return servico.obterPainel(periodicidade, ano, mes, dataReferencia);
    }
}
