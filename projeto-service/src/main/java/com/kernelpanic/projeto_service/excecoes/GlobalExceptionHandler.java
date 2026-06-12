package com.kernelpanic.projeto_service.excecoes;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.kernelpanic.projeto_service.dtos.ErroRespostaDTO;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EntidadeNaoEncontradaException.class)
    public ResponseEntity<ErroRespostaDTO> tratarEntidadeNaoEncontrada(EntidadeNaoEncontradaException ex) {
        ErroRespostaDTO erro = new ErroRespostaDTO(
                String.valueOf(HttpStatus.NOT_FOUND.value()),
                ex.getMessage(),
                ex.getDescricao());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(erro);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroRespostaDTO> tratarValidacao(MethodArgumentNotValidException ex) {
        StringBuilder descricao = new StringBuilder();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                descricao.append(error.getField()).append(": ").append(error.getDefaultMessage()).append("; "));

        ErroRespostaDTO erro = new ErroRespostaDTO(
                String.valueOf(HttpStatus.BAD_REQUEST.value()),
                "Erro de validacao",
                descricao.toString());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erro);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErroRespostaDTO> tratarArgumentoInvalido(IllegalArgumentException ex) {
        ErroRespostaDTO erro = new ErroRespostaDTO(
                String.valueOf(HttpStatus.BAD_REQUEST.value()),
                "Parametro invalido",
                ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erro);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErroRespostaDTO> tratarTipoParametroInvalido(MethodArgumentTypeMismatchException ex) {
        String nome = ex.getName();
        String valor = ex.getValue() == null ? "null" : ex.getValue().toString();

        ErroRespostaDTO erro = new ErroRespostaDTO(
                String.valueOf(HttpStatus.BAD_REQUEST.value()),
                "Parametro invalido",
                "Valor invalido para o parametro '" + nome + "': " + valor + ".");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erro);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErroRespostaDTO> tratarRuntime(RuntimeException ex) {
        ErroRespostaDTO erro = new ErroRespostaDTO(
                String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()),
                "Erro interno do servidor",
                ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(erro);
    }
}
