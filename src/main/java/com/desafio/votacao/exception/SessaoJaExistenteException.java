package com.desafio.votacao.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class SessaoJaExistenteException extends RuntimeException {
    public SessaoJaExistenteException(Long pautaId) {
        super("Já existe sessão de votação para esta pauta (pautaId=" + pautaId + ")");
    }
}