package com.desafio.votacao.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class OpcaoVotoInvalidaException extends RuntimeException {
    public OpcaoVotoInvalidaException(String valor) {
        super("Opção de voto inválida: " + valor);
    }
}