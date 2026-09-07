package com.desafio.votacao.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class AssociadoNaoHabilitadoException extends RuntimeException {

    public AssociadoNaoHabilitadoException(String associadoId) {
        super("Associado não habilitado para votação: " + associadoId);
    }
}
