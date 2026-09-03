package com.desafio.votacao.controller;

import com.desafio.votacao.dto.*;
import com.desafio.votacao.entity.Pauta;
import com.desafio.votacao.exception.*;
import com.desafio.votacao.service.VotingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/pautas")
@Tag(description = "API de Gerenciamento de Pautas e Votação", name = "pauta")
@RequiredArgsConstructor
public class PautaController {

    private final VotingService votingService = null;

    @PostMapping
    @Operation(summary = "Criar uma nova pauta", description = "Cadastra uma nova pauta para votação")
    @ApiResponse(responseCode = "201", description = "Pauta criada com sucesso",
            content = @Content(schema = @Schema(implementation = PautaResponse.class)))
    public ResponseEntity<PautaResponse> criarPauta(@Valid @RequestBody CriarPautaRequest request) {
        PautaResponse response = votingService.criarPauta(request);
        return ResponseEntity.status(201).body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar pauta por ID", description = "Retorna os dados de uma pauta específica")
    @ApiResponse(responseCode = "200", description = "Pauta encontrada",
            content = @Content(schema = @Schema(implementation = PautaResponse.class)))
    @ApiResponse(responseCode = "404", description = "Pauta não encontrada")
    public ResponseEntity<PautaResponse> buscarPauta(@PathVariable Long id) {
        Pauta pauta = votingService.pesquisarPauta(id);
        return ResponseEntity.ok(new PautaResponse(pauta.getId(), pauta.getTitulo(), pauta.getDescricao(), pauta.getCriadaEm()));
    }

    @PostMapping("/{id}/sessao")
    @Operation(summary = "Abrir sessão de votação", description = "Abre uma sessão de votação para a pauta especificada")
    @ApiResponse(responseCode = "200", description = "Sessão aberta com sucesso",
            content = @Content(schema = @Schema(implementation = SessaoResponse.class)))
    @ApiResponse(responseCode = "404", description = "Pauta não encontrada")
    @ApiResponse(responseCode = "409", description = "Já existe sessão para esta pauta")
    public ResponseEntity<SessaoResponse> abrirSessao(@PathVariable Long id,
                                                      @RequestBody(required = false) SessaoRequest request) {
        SessaoResponse response = votingService.abrirSessao(id, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/votos")
    @Operation(summary = "Registrar voto", description = "Registra o voto de um associado em uma pauta")
    @ApiResponse(responseCode = "200", description = "Voto registrado com sucesso",
            content = @Content(schema = @Schema(implementation = VotosRegistrarResponse.class)))
    @ApiResponse(responseCode = "400", description = "Voto inválido ou associado inválido")
    @ApiResponse(responseCode = "404", description = "Pauta ou sessão não encontrada")
    @ApiResponse(responseCode = "409", description = "Voto duplicado ou sessão encerrada")
    public ResponseEntity<VotosRegistrarResponse> registrarVoto(@PathVariable Long id,
                                                                @Valid @RequestBody VotarRequest request) {
        VotosRegistrarResponse response = votingService.votar(id, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/resultado")
    @Operation(summary = "Obter resultado da votação", description = "Retorna o total de votos SIM e NAO e o resultado")
    @ApiResponse(responseCode = "200", description = "Resultado da votação",
            content = @Content(schema = @Schema(implementation = ResultadoResponse.class)))
    @ApiResponse(responseCode = "404", description = "Pauta não encontrada")
    public ResponseEntity<ResultadoResponse> obterResultado(@PathVariable Long id) {
        ResultadoResponse response = votingService.resultado(id);
        return ResponseEntity.ok(response);
    }
}