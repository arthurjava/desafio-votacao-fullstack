package com.desafio.votacao.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class TituloJaExisteException extends RuntimeException {
    public TituloJaExisteException(String titulo) {
        super("Já existe pauta com este título: " + titulo);
    }
}