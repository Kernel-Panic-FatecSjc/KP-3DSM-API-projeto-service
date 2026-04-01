package com.kernelpanic.projeto_service.excecoes;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;



@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> tratarValidacao(MethodArgumentNotValidException ex){
        Map<String, String> erros = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error -> {
            erros.put(error.getField(), error.getDefaultMessage());
        });

        return erros;
    }

    @ExceptionHandler(RuntimeException.class)
    public Map<String, String> tratarRuntime(RuntimeException ex) {
        Map<String, String> erro = new HashMap<>();

        erro.put("Erro", ex.getMessage());
        return erro;
    }



}
