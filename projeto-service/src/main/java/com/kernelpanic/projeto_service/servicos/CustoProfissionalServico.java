package com.kernelpanic.projeto_service.servicos;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.kernelpanic.projeto_service.dtos.CustoProfissionalDTO;
import com.kernelpanic.projeto_service.dtos.HorasAprovadasAgregadoDTO;
import com.kernelpanic.projeto_service.dtos.UsuarioFinanceiroDTO;
import com.kernelpanic.projeto_service.entidades.Projeto;
import com.kernelpanic.projeto_service.enums.PeriodicidadeFinanceira;
import com.kernelpanic.projeto_service.repositorios.ProjetoRepositorio;

import lombok.RequiredArgsConstructor;

/**
 * Custo por profissional (× projeto) no período: horas aprovadas × custo/hora.
 * Reaproveita {@link IntegracaoFinanceiraCliente} (mesma base do indicador
 * financeiro) e resolve os nomes de projeto/usuário para exibição.
 */
@Service
@RequiredArgsConstructor
public class CustoProfissionalServico {

    private static final ZoneId ZONE_ID = ZoneId.of("America/Sao_Paulo");

    private final IntegracaoFinanceiraCliente integracao;
    private final ProjetoRepositorio projetoRepositorio;

    public List<CustoProfissionalDTO> calcular(PeriodicidadeFinanceira periodicidade, Integer ano, Integer mes, LocalDate dataReferencia) {
        Periodo periodo = resolverPeriodo(periodicidade, ano, mes, dataReferencia);

        List<HorasAprovadasAgregadoDTO> horas = integracao.buscarHorasAgregadas(periodo.inicio(), periodo.fim());

        Set<Long> usuarioIds = horas.stream()
                .map(HorasAprovadasAgregadoDTO::getUsuarioId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        Map<Long, UsuarioFinanceiroDTO> usuarios = integracao.buscarUsuariosFinanceiros(usuarioIds);
        Map<Long, String> nomesProjetos = projetoRepositorio.findAll().stream()
                .filter(p -> p.getId() != null)
                .collect(Collectors.toMap(Projeto::getId, Projeto::getNome));

        List<CustoProfissionalDTO> resultado = new ArrayList<>();
        for (HorasAprovadasAgregadoDTO item : horas) {
            UsuarioFinanceiroDTO usuario = usuarios.get(item.getUsuarioId());
            BigDecimal horasAprovadas = integracao.valorOuZero(item.getHorasAprovadas());
            BigDecimal custoTotal = horasAprovadas
                    .multiply(integracao.calcularCustoHora(usuario))
                    .setScale(2, RoundingMode.HALF_UP);

            CustoProfissionalDTO dto = new CustoProfissionalDTO();
            dto.setUsuarioId(item.getUsuarioId());
            dto.setUsuarioNome(usuario != null && usuario.getNome() != null
                    ? usuario.getNome()
                    : "Profissional " + item.getUsuarioId());
            dto.setProjetoId(item.getProjetoId());
            dto.setProjetoNome(nomesProjetos.getOrDefault(item.getProjetoId(), "Projeto " + item.getProjetoId()));
            dto.setHorasAprovadas(horasAprovadas);
            dto.setCustoTotal(custoTotal);
            resultado.add(dto);
        }
        return resultado;
    }

    private Periodo resolverPeriodo(PeriodicidadeFinanceira periodicidade, Integer ano, Integer mes, LocalDate dataReferencia) {
        PeriodicidadeFinanceira tipo = periodicidade == null ? PeriodicidadeFinanceira.MENSAL : periodicidade;
        LocalDate referencia = dataReferencia == null ? LocalDate.now(ZONE_ID) : dataReferencia;

        if (tipo == PeriodicidadeFinanceira.SEMANAL) {
            LocalDate inicio = referencia.with(DayOfWeek.MONDAY);
            return new Periodo(inicio, inicio.plusDays(6));
        }

        int anoBase = ano == null ? referencia.getYear() : ano;
        int mesBase = mes == null ? referencia.getMonthValue() : mes;
        YearMonth yearMonth = YearMonth.of(anoBase, mesBase);
        return new Periodo(yearMonth.atDay(1), yearMonth.atEndOfMonth());
    }

    private record Periodo(LocalDate inicio, LocalDate fim) {
    }
}
