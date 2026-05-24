package com.kernelpanic.projeto_service.dtos;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ProjetoCadastrarDTO {
    @NotBlank(message = "O nome do projeto é obrigatório")
    @Size(max = 100, message = "O nome não pode exceder 100 caracteres")
    private String nome;
    
    private String status;

    @PositiveOrZero(message = "O valor contratado nao pode ser negativo")
    private BigDecimal valorContratado;

    @Size(max = 300, message = "A descrição não pode exceder 300 caracteres")
    private String descricao;
    
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
    private LocalDateTime prazo;
}
