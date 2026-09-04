package com.desafio.votacao.controller;

import com.desafio.votacao.dto.SessaoRequest;
import com.desafio.votacao.dto.SessaoResponse;
import com.desafio.votacao.exception.*;
import com.desafio.votacao.service.VotingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/pautas")
@Tag(description = "API de Gerenciamento de Pautas e Votação", name = "sessao")
public class SessaoController {

    @Autowired
    private final VotingService votingService;

    public SessaoController(VotingService votingService) {
        this.votingService = votingService;
    }

    @Operation(summary = "Abrir sessão de votação", description = "Abre uma sessão de votação para a pauta especificada")
    @ApiResponse(responseCode = "200", description = "Sessão aberta com sucesso",
            content = @Content(schema = @Schema(implementation = SessaoResponse.class)))
    @ApiResponse(responseCode = "404", description = "Pauta não encontrada")
    @ApiResponse(responseCode = "409", description = "Já existe sessão para esta pauta")
    @PostMapping("/{id}/sessao")
    public ResponseEntity<SessaoResponse> abrirSessao(@PathVariable Long id,
                                                      @RequestBody(required = false) SessaoRequest request) {
        SessaoResponse response = votingService.abrirSessao(id, request);
        return ResponseEntity.ok(response);
    }
}