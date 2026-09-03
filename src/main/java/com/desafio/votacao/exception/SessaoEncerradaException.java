package com.desafio.votacao.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class SessaoEncerradaException extends RuntimeException {
    public SessaoEncerradaException(Long pautaId) {
        super("Sessão de votação encerrada para esta pauta (pautaId=" + pautaId + ")");
    }
}