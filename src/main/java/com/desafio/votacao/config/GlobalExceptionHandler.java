package com.desafio.votacao.config;

import com.desafio.votacao.exception.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(VotacaoDuplicadaException.class)
    public ResponseEntity<ErroResponse> handleVotacaoDuplicada(VotacaoDuplicadaException ex) {
        log.warn("Tentativa de voto duplicado: {}", ex.getMessage());
        ErroResponse erro = new ErroResponse(
                HttpStatus.CONFLICT.value(),
                ex.getMessage(),
                LocalDateTime.now().toString(),
                "/api/v1/pautas/voto"
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(erro);
    }

    @ExceptionHandler(SessaoEncerradaException.class)
    public ResponseEntity<ErroResponse> handleSessaoEncerrada(SessaoEncerradaException ex) {
        log.warn("Tentativa de voto em sessão encerrada: {}", ex.getMessage());
        ErroResponse erro = new ErroResponse(
                HttpStatus.CONFLICT.value(),
                ex.getMessage(),
                LocalDateTime.now().toString(),
                "/api/v1/pautas/voto"
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(erro);
    }

    @ExceptionHandler(SessaoNaoEncontradaException.class)
    public ResponseEntity<ErroResponse> handleSessaoNaoEncontrada(SessaoNaoEncontradaException ex) {
        log.warn("Sessão não encontrada: {}", ex.getMessage());
        ErroResponse erro = new ErroResponse(
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage(),
                LocalDateTime.now().toString(),
                "/api/v1/pautas"
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(erro);
    }

    @ExceptionHandler(PautaNaoEncontradaException.class)
    public ResponseEntity<ErroResponse> handlePautaNaoEncontrada(PautaNaoEncontradaException ex) {
        log.warn("Pauta não encontrada: {}", ex.getMessage());
        ErroResponse erro = new ErroResponse(
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage(),
                LocalDateTime.now().toString(),
                "/api/v1/pautas/{id}"
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(erro);
    }

    @ExceptionHandler({InvalidVoteOptionException.class, AssociadoIdInvalidoException.class, OpcaoVotoInvalidaException.class})
    public ResponseEntity<ErroResponse> handleBadRequest(Exception ex) {
        log.warn("Requisição inválida: {}", ex.getMessage());
        ErroResponse erro = new ErroResponse(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                LocalDateTime.now().toString(),
                "/api/v1/pautas/voto"
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erro);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErroResponse> handleUnexpected(Exception ex) {
        log.error("Erro inesperado: ", ex);
        ErroResponse erro = new ErroResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Erro interno do servidor",
                LocalDateTime.now().toString(),
                "/api/v1/pautas"
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(erro);
    }
}