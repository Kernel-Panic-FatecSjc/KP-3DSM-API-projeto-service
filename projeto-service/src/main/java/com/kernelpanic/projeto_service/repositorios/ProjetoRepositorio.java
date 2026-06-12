package com.kernelpanic.projeto_service.repositorios;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.kernelpanic.projeto_service.entidades.Projeto;

public interface ProjetoRepositorio extends JpaRepository<Projeto, Long> {

    @Query("""
            SELECT p.id AS id,
                   p.nome AS nome,
                   p.status AS status,
                   p.valorContratado AS valorContratado
            FROM Projeto p
            """)
    List<ProjetoFinanceiroProjection> buscarProjetosParaPainelFinanceiro();
    
    @Query("SELECT DISTINCT p FROM Projeto p LEFT JOIN p.profissionaisIds pid WHERE pid = :profissionalId OR p.responsavelId = :profissionalId")
    List<Projeto> findByProfissionalId(@Param("profissionalId") Long profissionalId);
    
    @Query("SELECT DISTINCT p FROM Projeto p LEFT JOIN p.profissionaisIds pid WHERE (pid = :profissionalId OR p.responsavelId = :profissionalId) AND (:dataInicio IS NULL OR p.dataCriacao >= :dataInicio) AND (:dataFim IS NULL OR p.prazo <= :dataFim)")
    List<Projeto> findByProfissionalIdAndDateRange(
            @Param("profissionalId") Long profissionalId,
            @Param("dataInicio") LocalDateTime dataInicio,
            @Param("dataFim") LocalDateTime dataFim);
}
