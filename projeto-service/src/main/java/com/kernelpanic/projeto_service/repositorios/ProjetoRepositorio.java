package com.kernelpanic.projeto_service.repositorios;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kernelpanic.projeto_service.entidades.Projeto;

public interface ProjetoRepositorio extends JpaRepository<Projeto, Long> {
    
}
