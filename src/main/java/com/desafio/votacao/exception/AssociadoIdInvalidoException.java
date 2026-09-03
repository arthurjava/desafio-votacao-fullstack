package com.desafio.votacao.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class AssociadoIdInvalidoException extends RuntimeException {
    public AssociadoIdInvalidoException(String campo) {
        super("Campo " + campo + " inválido ou ausente");
    }
}