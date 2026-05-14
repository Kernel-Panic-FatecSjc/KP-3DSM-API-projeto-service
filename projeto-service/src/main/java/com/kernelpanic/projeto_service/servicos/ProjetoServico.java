package com.kernelpanic.projeto_service.servicos;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kernelpanic.projeto_service.dtos.ProjetoAtualizarDTO;
import com.kernelpanic.projeto_service.dtos.ProjetoCadastrarDTO;
import com.kernelpanic.projeto_service.dtos.ProjetoExibirDTO;
import com.kernelpanic.projeto_service.entidades.Projeto;
import com.kernelpanic.projeto_service.modelo.ProjetoAtualizador;
import com.kernelpanic.projeto_service.modelo.ProjetoSelecionador;
import com.kernelpanic.projeto_service.repositorios.ProjetoRepositorio;
import com.kernelpanic.projeto_service.excecoes.EntidadeNaoEncontradaException;

@Service
public class ProjetoServico {

    @Autowired
    private ProjetoRepositorio repositorio;

    @Autowired
    private ProjetoSelecionador selecionador;

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
        
        ProjetoAtualizador atualizador = new ProjetoAtualizador();
        atualizador.atualizar(projeto, atualizacao);
        repositorio.save(projeto);
    }

    public void deletarPorId(Long id) {
        Projeto projeto = repositorio.findById(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException(
                    "Exclusão negada", 
                    "Não foi possível localizar o projeto de ID " + id + " para remover."));
        repositorio.delete(projeto);
    }

    private ProjetoExibirDTO converterParaExibirDTO(Projeto projeto) {
        ProjetoExibirDTO dto = new ProjetoExibirDTO();
        dto.setId(projeto.getId());
        dto.setNome(projeto.getNome());
        dto.setDescricao(projeto.getDescricao());
        dto.setStatus(projeto.getStatus());
        dto.setPrazo(projeto.getPrazo());
        dto.setValorContratado(projeto.getValorContratado());
        dto.setDataCriacao(projeto.getDataCriacao());
        return dto;
    }

    public void cadastrarViaDTO(ProjetoCadastrarDTO dto) {
        Projeto projeto = new Projeto();
        projeto.setNome(dto.getNome());
        projeto.setDescricao(dto.getDescricao());
        projeto.setPrazo(dto.getPrazo());
        projeto.setValorContratado(dto.getValorContratado());

        this.cadastrar(projeto);
    }

    public void atualizarViaDTO(Long id, ProjetoAtualizarDTO dto) {
        Projeto projeto = new Projeto();
        projeto.setId(id);
        projeto.setNome(dto.getNome());
        projeto.setDescricao(dto.getDescricao());
        projeto.setPrazo(dto.getPrazo());
        projeto.setValorContratado(dto.getValorContratado());

        this.atualizar(projeto);
    }
}
