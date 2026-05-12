package com.kernelpanic.projeto_service.excecoes;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import com.kernelpanic.projeto_service.dtos.ErroRespostaDTO;

@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(EntidadeNaoEncontradaException.class)
    public ResponseEntity<ErroRespostaDTO> tratarEntidadeNaoEncontrada(EntidadeNaoEncontradaException ex) {
        ErroRespostaDTO erro = new ErroRespostaDTO(
            String.valueOf(HttpStatus.NOT_FOUND.value()),
            ex.getMessage(),
            ex.getDescricao()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(erro);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroRespostaDTO> tratarValidacao(MethodArgumentNotValidException ex){
        StringBuilder descricao = new StringBuilder();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            descricao.append(error.getField()).append(": ").append(error.getDefaultMessage()).append("; ");
        });

        ErroRespostaDTO erro = new ErroRespostaDTO(
            String.valueOf(HttpStatus.NOT_FOUND.value()),
            "Erro de validação",
            descricao.toString()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erro);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErroRespostaDTO> tratarRuntime(RuntimeException ex) {
        ErroRespostaDTO erro = new ErroRespostaDTO(
            String.valueOf(HttpStatus.NOT_FOUND.value()),
            "Erro interno do servidor",
            ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(erro);
    }

}
