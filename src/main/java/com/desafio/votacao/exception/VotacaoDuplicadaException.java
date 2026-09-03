package com.desafio.votacao.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class VotacaoDuplicadaException extends RuntimeException {
    public VotacaoDuplicadaException(Long pautaId, String associadoId) {
        super("Associado já realizou voto nesta pauta (pautaId=" + pautaId + ", associadoId=" + associadoId + ")");
    }
}