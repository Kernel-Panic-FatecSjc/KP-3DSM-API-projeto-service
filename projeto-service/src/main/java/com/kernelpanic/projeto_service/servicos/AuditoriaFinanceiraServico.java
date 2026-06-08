package com.kernelpanic.projeto_service.servicos;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.kernelpanic.projeto_service.dtos.AuditoriaFinanceiraDTO;
import com.kernelpanic.projeto_service.entidades.AuditoriaFinanceira;
import com.kernelpanic.projeto_service.enums.TipoEventoFinanceiro;
import com.kernelpanic.projeto_service.repositorios.AuditoriaFinanceiraRepositorio;

import lombok.RequiredArgsConstructor;

/**
 * Registra e consulta a trilha de auditoria financeira dos projetos.
 */
@Service
@RequiredArgsConstructor
public class AuditoriaFinanceiraServico {

    private final AuditoriaFinanceiraRepositorio repositorio;

    public void registrar(Long projetoId, String projetoNome, TipoEventoFinanceiro tipo, String descricao, BigDecimal impacto) {
        AuditoriaFinanceira registro = new AuditoriaFinanceira();
        registro.setProjetoId(projetoId);
        registro.setProjetoNome(projetoNome);
        registro.setTipo(tipo);
        registro.setDescricao(descricao);
        registro.setImpacto(impacto == null ? BigDecimal.ZERO : impacto);
        repositorio.save(registro);
    }

    public List<AuditoriaFinanceiraDTO> listar(Long projetoId) {
        List<AuditoriaFinanceira> registros = projetoId == null
                ? repositorio.findAllByOrderByDataHoraDesc()
                : repositorio.findByProjetoIdOrderByDataHoraDesc(projetoId);
        return registros.stream().map(this::converter).collect(Collectors.toList());
    }

    private AuditoriaFinanceiraDTO converter(AuditoriaFinanceira registro) {
        return new AuditoriaFinanceiraDTO(
                registro.getId(),
                registro.getDataHora(),
                registro.getProjetoId(),
                registro.getProjetoNome(),
                registro.getTipo() == null ? null : registro.getTipo().name(),
                registro.getDescricao(),
                registro.getImpacto());
    }
}
