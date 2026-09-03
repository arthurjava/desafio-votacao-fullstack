package com.desafio.votacao.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidVoteOptionException extends RuntimeException {
    public InvalidVoteOptionException(String voto) {
        super("Opção de voto inválida: " + voto + ". Use 'SIM' ou 'NAO'");
    }
}