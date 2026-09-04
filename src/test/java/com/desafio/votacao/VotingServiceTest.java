package com.desafio.votacao;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.desafio.votacao.dto.CriarPautaRequest;
import com.desafio.votacao.dto.VotarRequest;
import com.desafio.votacao.entity.Pauta;
import com.desafio.votacao.entity.Voto.VoteOpcao;
import com.desafio.votacao.entity.SessaoVotacao;
import com.desafio.votacao.exception.*;
import com.desafio.votacao.service.VotingService;

import org.mockito.MockitoAnnotations;

/**
 * Test class for VotingService.
 * Tests the voting service with Mockito mocks for repository and integration dependencies.
 */
class VotingServiceTest {

    private VotingService votingService;
    private Pauta pauta;
    private SessaoVotacao sessao;

    /**
     * Setup method run before each test.
     * Initializes mocks and test data.
     */
    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        votingService = new VotingService(
                mock(PautaRepository.class),
                mock(SessaoVotacaoRepository.class),
                mock(VotoRepository.class),
                mock(AssociadoClient.class)
        );

        pauta = new Pauta("Titulo da Pauta", "Descricao da pauta");
        sessao = new SessaoVotacao(pauta.getId(), LocalDateTime.now().plusSeconds(60));
    }

    /**
     * Test to verify that the VotingService is properly initialized with mocks.
     */
    @Test
    @DisplayName("Deve inicializar o VotingService com mocks")
    public void testServiceInicializacaoComMocks() {
        assertNotNull(votingService);
    }

    /**
     * Test to verify that a new pauta can be created.
     */
    @Test
    @DisplayName("Deve criar uma nova pauta")
    public void testCriarPauta() {
        when(pautaRepository.findByTitulo("Titulo da Pauta")).thenReturn(java.util.Optional.empty());
        when(pautaRepository.save(any(Pauta.class))).thenReturn(pauta);

        CriarPautaRequest request = new CriarPautaRequest("Titulo da Pauta", "Descricao da pauta");
        com.desafio.votacao.dto.PautaResponse response = votingService.criarPauta(request);

        assertNotNull(response);
        assertEquals("Titulo da Pauta", response.getTitulo());
        assertEquals("Descricao da pauta", response.getDescricao());
    }

    /**
     * Test to verify that an exception is thrown when trying to create a pauta with a duplicate title.
     */
    @Test
    @DisplayName("Deve lancar excecao ao tentar criar pauta com titulo duplicado")
    public void testCriarPautaTituloDuplicado() {
        when(pautaRepository.findByTitulo("Titulo da Pauta")).thenReturn(java.util.Optional.of(pauta));

        CriarPautaRequest request = new CriarPautaRequest("Titulo da Pauta", "Descricao");
        assertThrows(TituloJaExisteException.class, () -> votingService.criarPauta(request));
    }

    /**
     * Test to verify that a session can be opened.
     */
    @Test
    @DisplayName("Deve abrir uma sessao de votacao")
    public void testAbrirSessao() {
        when(sessaoVotacaoRepository.findByPautaId(pauta.getId())).thenReturn(java.util.Optional.empty());
        when(sessaoVotacaoRepository.save(any(SessaoVotacao.class))).thenReturn(sessao);

        com.desafio.votacao.dto.SessaoRequest request = new com.desafio.votacao.dto.SessaoRequest();
        request.setDuracaoEmSegundos(120);

        com.desafio.votacao.dto.SessaoResponse response = votingService.abrirSessao(pauta.getId(), request);

        assertNotNull(response);
        assertEquals(pauta.getId(), response.getPautaId());
    }

    /**
     * Test to verify that an exception is thrown when trying to open a duplicate session.
     */
    @Test
    @DisplayName("Deve lancar excecao ao abrir sessao duplicada")
    public void testAbrirSessaoDuplicada() {
        when(sessaoVotacaoRepository.findByPautaId(pauta.getId())).thenReturn(java.util.Optional.of(sessao));

        assertThrows(SessaoJaExistenteException.class, () -> votingService.abrirSessao(pauta.getId(), null));
    }

    /**
     * Test to verify that a vote can be registered validly.
     */
    @Test
    @DisplayName("Deve registrar um voto valido")
    public void testVotarValido() {
        when(associadoClient.consultar(anyString())).thenReturn(StatusVotacao.ABLE_TO_VOTE);
        when(sessaoVotacaoRepository.findAtivaByPautaId(pauta.getId())).thenReturn(java.util.Optional.of(sessao));
        when(votoRepository.existsByPautaIdAndAssociadoId(pauta.getId(), "assoc123")).thenReturn(false);
        when(votoRepository.save(any(Voto.class))).thenReturn(new Voto(pauta.getId(), "assoc123", VoteOpcao.SIM));

        VotarRequest request = new VotarRequest("assoc123", "SIM");

        assertDoesNotThrow(() -> votingService.votar(pauta.getId(), request));
    }

    /**
     * Test to verify that an exception is thrown when voting with an invalid CPF.
     */
    @Test
    @DisplayName("Deve lancar excecao ao votar com CPF invalido")
    public void testVotarCpfInvalido() {
        when(associadoClient.consultar(anyString())).thenReturn(StatusVotacao.CPF_INVALIDO);

        VotarRequest request = new VotarRequest("invalid_cpf", "SIM");

        assertThrows(CpfInvalidoException.class, () -> votingService.votar(pauta.getId(), request));
    }

    /**
     * Test to verify that an exception is thrown when voting in a closed session.
     */
    @Test
    @DisplayName("Deve lancar excecao ao votar em sessao encerrada")
    public void testVotarSessaoEncerrada() {
        when(associadoClient.consultar(anyString())).thenReturn(StatusVotacao.ABLE_TO_VOTE);
        when(sessaoVotacaoRepository.findAtivaByPautaId(pauta.getId())).thenReturn(java.util.Optional.empty());

        VotarRequest request = new VotarRequest("assoc123", "SIM");

        assertThrows(SessaoEncerradaException.class, () -> votingService.votar(pauta.getId(), request));
    }

    /**
     * Test to verify that an exception is thrown when voting with an invalid option.
     */
    @Test
    @DisplayName("Deve lancar excecao ao votar com opcao invalida")
    public void testVotarOpcaoInvalida() {
        when(associadoClient.consultar(anyString())).thenReturn(StatusVotacao.ABLE_TO_VOTE);

        VotarRequest request = new VotarRequest("assoc123", "INVALIDO");

        assertThrows(InvalidVoteOptionException.class, () -> votingService.votar(pauta.getId(), request));
    }

    /**
     * Test to verify that the voting result is returned correctly.
     */
    @Test
    @DisplayName("Deve retornar resultado da votacao")
    public void testResultado() {
        when(pautaRepository.findById(pauta.getId())).thenReturn(java.util.Optional.of(pauta));
        when(votoRepository.countByPautaId(pauta.getId())).thenReturn(3L);
        when(votoRepository.countByPautaId(pauta.getId())).thenReturn(5L);

        com.desafio.votacao.dto.ResultadoResponse response = votingService.resultado(pauta.getId());

        assertNotNull(response);
        assertEquals(3L, response.getSim());
        assertEquals(2L, response.getNao());
        assertEquals(5L, response.getTotal());
        assertEquals("APROVADA", response.getResultado());
    }

    /**
     * Test to verify that an exception is thrown when trying to find a non-existent pauta.
     */
    @Test
    @DisplayName("Deve lancar excecao ao buscar pauta nao existente")
    public void testPautaNaoEncontrada() {
        when(pautaRepository.findById(1L)).thenReturn(java.util.Optional.empty());

        assertThrows(PautaNaoEncontradaException.class, () -> votingService.pesquisarPauta(1L));
    }

    /**
     * Test to verify that an exception is thrown when trying to get the result of a non-existent pauta.
     */
    @Test
    @DisplayName("Deve lancar excecao ao obter resultado de pauta nao existente")
    public void testResultadoPautaNaoEncontrada() {
        when(pautaRepository.findById(1L)).thenReturn(java.util.Optional.empty());

        assertThrows(PautaNaoEncontradaException.class, () -> votingService.resultado(1L));
    }
}