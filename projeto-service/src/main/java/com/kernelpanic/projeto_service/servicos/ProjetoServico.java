package com.kernelpanic.projeto_service.servicos;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kernelpanic.projeto_service.dtos.ProjetoAtualizarDTO;
import com.kernelpanic.projeto_service.dtos.ProjetoCadastrarDTO;
import com.kernelpanic.projeto_service.dtos.ProjetoExibirDTO;
import com.kernelpanic.projeto_service.entidades.Projeto;
import com.kernelpanic.projeto_service.enums.TipoEventoFinanceiro;
import com.kernelpanic.projeto_service.excecoes.EntidadeNaoEncontradaException;
import com.kernelpanic.projeto_service.modelo.ProjetoAtualizador;
import com.kernelpanic.projeto_service.modelo.ProjetoSelecionador;
import com.kernelpanic.projeto_service.repositorios.ProjetoRepositorio;

@Service
public class ProjetoServico {

    @Autowired
    private ProjetoRepositorio repositorio;

    @Autowired
    private ProjetoSelecionador selecionador;

    @Autowired
    private AuditoriaFinanceiraServico auditoria;

    public List<ProjetoExibirDTO> obterTodos() {
        List<Projeto> projetos = repositorio.findAll();
        return projetos.stream()
                .map(this::converterParaExibirDTO)
                .collect(Collectors.toList());
    }

    public ProjetoExibirDTO obterPorId(Long id) {
        List<Projeto> projetos = repositorio.findAll();
        
        Projeto projeto = selecionador.selecionar(projetos, id);
        if (projeto == null) {
            throw new EntidadeNaoEncontradaException(
                "Projeto não encontrado", 
                "Não foi possível localizar um projeto com o ID: " + id
            );
        }
        
        return converterParaExibirDTO(projeto);
    }

    public void cadastrar(Projeto projeto) {
        repositorio.save(projeto);
    }

    public void atualizar(Projeto atualizacao) {
        Projeto projeto = repositorio.findById(atualizacao.getId())
                .orElseThrow(() -> new EntidadeNaoEncontradaException(
                    "Atualização impossível", 
                    "O projeto de ID " + atualizacao.getId() + " não foi encontrado no banco."));
        
        BigDecimal valorAntigo = projeto.getValorContratado();

        ProjetoAtualizador atualizador = new ProjetoAtualizador();
        atualizador.atualizar(projeto, atualizacao);
        repositorio.save(projeto);

        BigDecimal impacto = diferenca(projeto.getValorContratado(), valorAntigo);
        if (impacto.signum() != 0) {
            auditoria.registrar(projeto.getId(), projeto.getNome(),
                    TipoEventoFinanceiro.VALOR_CONTRATADO_ALTERADO, "Valor contratado alterado", impacto);
        }
    }

    public void deletarPorId(Long id) {
        Projeto projeto = repositorio.findById(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException(
                    "Exclusão negada", 
                    "Não foi possível localizar o projeto de ID " + id + " para remover."));
        repositorio.delete(projeto);

        BigDecimal valor = projeto.getValorContratado();
        auditoria.registrar(projeto.getId(), projeto.getNome(),
                TipoEventoFinanceiro.PROJETO_REMOVIDO, "Projeto removido",
                valor == null ? null : valor.negate());
    }

    private ProjetoExibirDTO converterParaExibirDTO(Projeto projeto) {
        ProjetoExibirDTO dto = new ProjetoExibirDTO();
        dto.setId(projeto.getId());
        dto.setNome(projeto.getNome());
        dto.setDescricao(projeto.getDescricao());
        dto.setStatus(projeto.getStatus());
        dto.setValorContratado(projeto.getValorContratado());
        dto.setPrazo(projeto.getPrazo());
        dto.setValorContratado(projeto.getValorContratado());
        dto.setDataCriacao(projeto.getDataCriacao());
        dto.setDataInicio(projeto.getDataCriacao());
        dto.setDataFim(projeto.getPrazo());
        dto.setResponsavelId(projeto.getResponsavelId());
        dto.setProfissionaisIds(projeto.getProfissionaisIds() != null ? new ArrayList<>(projeto.getProfissionaisIds()) : new ArrayList<>());
        return dto;
    }

    public ProjetoExibirDTO cadastrarViaDTO(ProjetoCadastrarDTO dto) {
        Projeto projeto = new Projeto();
        projeto.setNome(dto.getNome());
        projeto.setDescricao(dto.getDescricao());
        projeto.setStatus(dto.getStatus());
        projeto.setValorContratado(dto.getValorContratado());
        projeto.setPrazo(dto.getPrazo());
        projeto.setStatus(dto.getStatus());
        projeto.setValorContratado(dto.getValorContratado());
        projeto.setResponsavelId(dto.getResponsavelId());
        projeto.setProfissionaisIds(dto.getProfissionaisIds());
        this.cadastrar(projeto);

        auditoria.registrar(projeto.getId(), projeto.getNome(),
                TipoEventoFinanceiro.PROJETO_CRIADO, "Projeto cadastrado", projeto.getValorContratado());

        return converterParaExibirDTO(projeto);
    }

    public void atualizarViaDTO(Long id, ProjetoAtualizarDTO dto) {
        Projeto projeto = new Projeto();
        projeto.setId(id);
        projeto.setNome(dto.getNome());
        projeto.setDescricao(dto.getDescricao());
        projeto.setStatus(dto.getStatus());
        projeto.setValorContratado(dto.getValorContratado());
        projeto.setPrazo(dto.getPrazo());
        projeto.setValorContratado(dto.getValorContratado());
        projeto.setResponsavelId(dto.getResponsavelId());
        projeto.setProfissionaisIds(dto.getProfissionaisIds());
        this.atualizar(projeto);
    }

    public List<ProjetoExibirDTO> obterProjetosPorProfissional(Long profissionalId) {
        List<Projeto> projetos = repositorio.findByProfissionalId(profissionalId);
        return projetos.stream()
                .map(this::converterParaExibirDTO)
                .collect(Collectors.toList());
    }

    public List<ProjetoExibirDTO> obterProjetosPorProfissionalComPeriodo(
            Long profissionalId, 
            LocalDateTime dataInicio, 
            LocalDateTime dataFim) {
        List<Projeto> projetos = repositorio.findByProfissionalIdAndDateRange(
            profissionalId,
            dataInicio,
            dataFim
        );
        return projetos.stream()
                .map(this::converterParaExibirDTO)
                .collect(Collectors.toList());
    }

    private BigDecimal diferenca(BigDecimal novo, BigDecimal antigo) {
        BigDecimal n = novo == null ? BigDecimal.ZERO : novo;
        BigDecimal a = antigo == null ? BigDecimal.ZERO : antigo;
        return n.subtract(a);
    }
}
