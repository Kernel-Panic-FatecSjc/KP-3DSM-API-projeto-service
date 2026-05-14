package com.kernelpanic.projeto_service.dtos;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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

    @Size(max = 300)
    private String descricao;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime prazo;

    private Float valorContratado;

    private Long responsavelId;

    private List<Long> profissionaisIds;
}