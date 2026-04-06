package com.kernelpanic.projeto_service.dtos;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProjetoExibirDTO {
    @NotNull(message = "O ID do projeto é obrigatório")
    private Long id;
    
    @NotBlank(message = "O nome do projeto é obrigatório")
    private String nome;
    
    private String descricao;

    private String status;
    
    private LocalDateTime prazo;
    
    @NotNull(message = "A data de criação é obrigatória")
    private LocalDateTime dataCriacao;
}
