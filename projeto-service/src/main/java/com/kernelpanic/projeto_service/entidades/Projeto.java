package com.kernelpanic.projeto_service.entidades;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "projetos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Projeto {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "nome", nullable = false, length = 100)
    private String nome;
    
    @Column(name = "descricao", length = 300)
    private String descricao;

    @Column(name = "status", length = 300)
    private String status;

    @Column(name = "valor_contratado", precision = 14, scale = 2)
    private BigDecimal valorContratado;
    
    @Column(name = "prazo")
    private LocalDateTime prazo;
    @CreationTimestamp
    @Column(name = "data_criacao", nullable = false, updatable = false)
    private LocalDateTime dataCriacao;

    @Column(name = "responsavel_id")
    private Long responsavelId;

    @ElementCollection
    @CollectionTable(name = "projeto_profissionais", joinColumns = @JoinColumn(name = "projeto_id"))
    @Column(name = "profissional_id")
    private List<Long> profissionaisIds;
}
