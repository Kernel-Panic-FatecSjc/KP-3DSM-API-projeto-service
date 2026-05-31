package com.kernelpanic.projeto_service.controles;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kernelpanic.projeto_service.dtos.CustoProfissionalDTO;
import com.kernelpanic.projeto_service.enums.PeriodicidadeFinanceira;
import com.kernelpanic.projeto_service.servicos.CustoProfissionalServico;

import lombok.RequiredArgsConstructor;

/**
 * Custo por profissional (× projeto) no período. Alimenta o gráfico
 * "Custo por profissional" do dashboard financeiro.
 */
@RestController
@RequestMapping("/projeto")
@RequiredArgsConstructor
public class ControleCustoProfissional {

    private final CustoProfissionalServico servico;

    @GetMapping("/custo-profissional")
    public List<CustoProfissionalDTO> custoProfissional(
            @RequestParam(defaultValue = "MENSAL") PeriodicidadeFinanceira periodicidade,
            @RequestParam(required = false) Integer ano,
            @RequestParam(required = false) Integer mes,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataReferencia) {
        return servico.calcular(periodicidade, ano, mes, dataReferencia);
    }
}
