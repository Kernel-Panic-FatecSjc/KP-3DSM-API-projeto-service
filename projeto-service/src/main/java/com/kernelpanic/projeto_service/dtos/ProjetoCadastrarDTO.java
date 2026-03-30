package com.kernelpanic.projeto_service.dtos;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ProjetoCadastrarDTO {
    @NotBlank(message = "O nome do projeto é obrigatório")
    @Size(max = 100, message = "O nome não pode exceder 100 caracteres")
    private String nome;
    
    @Size(max = 300, message = "A descrição não pode exceder 300 caracteres")
    private String descricao;
    
    private LocalDateTime prazo;
}
