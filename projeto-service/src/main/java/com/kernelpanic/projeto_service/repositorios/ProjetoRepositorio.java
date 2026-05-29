package com.kernelpanic.projeto_service.repositorios;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.kernelpanic.projeto_service.entidades.Projeto;

public interface ProjetoRepositorio extends JpaRepository<Projeto, Long> {
    
    @Query("SELECT p FROM Projeto p WHERE " +
           "FUNCTION('JSON_CONTAINS', p.profissionaisIds, CAST(:profissionalId AS string)) = true OR " +
           "p.responsavelId = :profissionalId")
    List<Projeto> findByProfissionalId(@Param("profissionalId") Long profissionalId);
    
    @Query("SELECT p FROM Projeto p WHERE " +
           "(FUNCTION('JSON_CONTAINS', p.profissionaisIds, CAST(:profissionalId AS string)) = true OR " +
           "p.responsavelId = :profissionalId) AND " +
           "(:dataInicio IS NULL OR p.dataCriacao >= :dataInicio) AND " +
           "(:dataFim IS NULL OR p.prazo <= :dataFim)")
    List<Projeto> findByProfissionalIdAndDateRange(
            @Param("profissionalId") Long profissionalId,
            @Param("dataInicio") LocalDateTime dataInicio,
            @Param("dataFim") LocalDateTime dataFim);
}