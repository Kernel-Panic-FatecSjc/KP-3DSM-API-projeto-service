package com.kernelpanic.projeto_service.dtos;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ProjetoAtualizarDTO {

    @NotNull
    private Long id;

    @NotBlank
    @Size(max = 100)
    private String nome;

    private String status;

    @PositiveOrZero(message = "O valor contratado nao pode ser negativo")
    private BigDecimal valorContratado;
    
    @Size(max = 300, message = "A descrição não pode exceder 300 caracteres")
    private String descricao;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime prazo;

    private Long responsavelId;

    private List<Long> profissionaisIds;
}