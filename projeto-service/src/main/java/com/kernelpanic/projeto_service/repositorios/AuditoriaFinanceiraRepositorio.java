package com.kernelpanic.projeto_service.repositorios;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kernelpanic.projeto_service.entidades.AuditoriaFinanceira;

@Repository
public interface AuditoriaFinanceiraRepositorio extends JpaRepository<AuditoriaFinanceira, Long> {

    List<AuditoriaFinanceira> findAllByOrderByDataHoraDesc();

    List<AuditoriaFinanceira> findByProjetoIdOrderByDataHoraDesc(Long projetoId);
}
