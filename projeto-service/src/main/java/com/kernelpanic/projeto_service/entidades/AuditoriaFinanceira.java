package com.kernelpanic.projeto_service.entidades;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.kernelpanic.projeto_service.enums.TipoEventoFinanceiro;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Registro da trilha de auditoria financeira: cada alteração de projeto com
 * impacto monetário (criação, mudança de valor contratado, remoção).
 */
@Entity
@Table(name = "auditoria_financeira")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuditoriaFinanceira {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreationTimestamp
    @Column(name = "data_hora", nullable = false, updatable = false)
    private LocalDateTime dataHora;

    @Column(name = "projeto_id")
    private Long projetoId;

    @Column(name = "projeto_nome", length = 100)
    private String projetoNome;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", length = 40, nullable = false)
    private TipoEventoFinanceiro tipo;

    @Column(name = "descricao", length = 300)
    private String descricao;

    @Column(name = "impacto", precision = 14, scale = 2)
    private BigDecimal impacto;
}
