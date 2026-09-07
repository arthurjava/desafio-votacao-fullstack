package com.desafio.votacao;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.desafio.votacao.dto.CriarPautaRequest;
import com.desafio.votacao.dto.VotarRequest;
import com.desafio.votacao.dto.SessaoRequest;
import com.desafio.votacao.entity.Pauta;
import com.desafio.votacao.entity.SessaoVotacao;
import com.desafio.votacao.entity.Voto;
import com.desafio.votacao.entity.Voto.VoteOpcao;
import com.desafio.votacao.exception.*;
import com.desafio.votacao.integration.AssociadoClient;
import com.desafio.votacao.integration.StatusVotacao;
import com.desafio.votacao.repository.PautaRepository;
import com.desafio.votacao.repository.SessaoVotacaoRepository;
import com.desafio.votacao.repository.VotoRepository;
import com.desafio.votacao.service.VotingService;

@ExtendWith(MockitoExtension.class)
class VotingServiceTest {

    @Mock
    private PautaRepository pautaRepository;

    @Mock
    private SessaoVotacaoRepository sessaoVotacaoRepository;

    @Mock
    private VotoRepository votoRepository;

    @Mock
    private AssociadoClient associadoClient;

    @InjectMocks
    private VotingService votingService;

    private Pauta criarPautaPadrao() {
        Pauta pauta = new Pauta("Titulo da Pauta", "Descricao da pauta");
        pauta.setId(1L);
        pauta.setCriadaEm(LocalDateTime.of(2026, 9, 6, 10, 0, 0));
        return pauta;
    }

    private SessaoVotacao criarSessaoPadrao(Pauta pauta, int duracaoSegundos) {
        SessaoVotacao sessao = new SessaoVotacao();
        sessao.setId(1L);
        sessao.setPautaId(pauta.getId());
        sessao.setAbertaEm(LocalDateTime.now());
        sessao.setEncerraEm(LocalDateTime.now().plusSeconds(duracaoSegundos));
        return sessao;
    }

    @Test
    @DisplayName("Deve inicializar o VotingService corretamente")
    void testServiceInicializacaoComMocks() {
        assertNotNull(votingService);
    }

    @Test
    @DisplayName("Deve criar uma nova pauta com sucesso")
    void testCriarPauta() {
        Pauta pauta = criarPautaPadrao();
        when(pautaRepository.findByTitulo("Titulo da Pauta")).thenReturn(Optional.empty());
        when(pautaRepository.save(any(Pauta.class))).thenReturn(pauta);

        CriarPautaRequest request = new CriarPautaRequest("Titulo da Pauta", "Descricao da pauta");
        var response = votingService.criarPauta(request);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Titulo da Pauta", response.getTitulo());
        assertEquals("Descricao da pauta", response.getDescricao());
        verify(pautaRepository, times(1)).save(any(Pauta.class));
    }

    @Test
    @DisplayName("Deve lancar excecao ao tentar criar pauta com titulo duplicado")
    void testCriarPautaTituloDuplicado() {
        Pauta pauta = criarPautaPadrao();
        when(pautaRepository.findByTitulo("Titulo da Pauta")).thenReturn(Optional.of(pauta));

        CriarPautaRequest request = new CriarPautaRequest("Titulo da Pauta", "Descricao");
        assertThrows(TituloJaExisteException.class, () -> votingService.criarPauta(request));
        verify(pautaRepository, never()).save(any(Pauta.class));
    }

    @Test
    @DisplayName("Deve buscar pauta existente")
    void testPesquisarPautaSucesso() {
        Pauta pauta = criarPautaPadrao();
        when(pautaRepository.findById(1L)).thenReturn(Optional.of(pauta));

        Pauta encontrada = votingService.pesquisarPauta(1L);

        assertNotNull(encontrada);
        assertEquals(1L, encontrada.getId());
    }

    @Test
    @DisplayName("Deve lancar excecao ao buscar pauta nao existente")
    void testPesquisarPautaNaoEncontrada() {
        when(pautaRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(PautaNaoEncontradaException.class, () -> votingService.pesquisarPauta(99L));
    }

    @Test
    @DisplayName("Deve abrir sessao de votacao com duracao customizada")
    void testAbrirSessaoComDuracao() {
        Pauta pauta = criarPautaPadrao();
        SessaoVotacao sessao = criarSessaoPadrao(pauta, 120);
        when(pautaRepository.findById(1L)).thenReturn(Optional.of(pauta));
        when(sessaoVotacaoRepository.findByPautaId(1L)).thenReturn(Optional.empty());
        when(sessaoVotacaoRepository.save(any(SessaoVotacao.class))).thenReturn(sessao);

        SessaoRequest request = new SessaoRequest();
        request.setDuracaoEmSegundos(120);
        var response = votingService.abrirSessao(1L, request);

        assertNotNull(response);
        assertEquals(1L, response.getPautaId());
        verify(sessaoVotacaoRepository, times(1)).save(any(SessaoVotacao.class));
    }

    @Test
    @DisplayName("Deve abrir sessao de votacao com duracao default 60s quando request null")
    void testAbrirSessaoSemRequestDefault60() {
        Pauta pauta = criarPautaPadrao();
        SessaoVotacao sessao = criarSessaoPadrao(pauta, 60);
        when(pautaRepository.findById(1L)).thenReturn(Optional.of(pauta));
        when(sessaoVotacaoRepository.findByPautaId(1L)).thenReturn(Optional.empty());
        when(sessaoVotacaoRepository.save(any(SessaoVotacao.class))).thenReturn(sessao);

        var response = votingService.abrirSessao(1L, null);

        assertNotNull(response);
        verify(sessaoVotacaoRepository, times(1)).save(any(SessaoVotacao.class));
    }

    @Test
    @DisplayName("Deve lancar excecao ao abrir sessao duplicada para mesma pauta")
    void testAbrirSessaoDuplicada() {
        Pauta pauta = criarPautaPadrao();
        SessaoVotacao sessao = criarSessaoPadrao(pauta, 60);
        when(pautaRepository.findById(1L)).thenReturn(Optional.of(pauta));
        when(sessaoVotacaoRepository.findByPautaId(1L)).thenReturn(Optional.of(sessao));

        assertThrows(SessaoJaExistenteException.class, () -> votingService.abrirSessao(1L, null));
        verify(sessaoVotacaoRepository, never()).save(any(SessaoVotacao.class));
    }

    @Test
    @DisplayName("Deve registrar um voto valido com SIM")
    void testVotarValidoSim() {
        Pauta pauta = criarPautaPadrao();
        SessaoVotacao sessao = criarSessaoPadrao(pauta, 60);
        Voto votoSalvo = new Voto(1L, "12345678901", VoteOpcao.SIM);
        votoSalvo.setId(1L);

        when(associadoClient.consultar("12345678901")).thenReturn(StatusVotacao.ABLE_TO_VOTE);
        when(votoRepository.existsByPautaIdAndAssociadoId(1L, "12345678901")).thenReturn(false);
        when(sessaoVotacaoRepository.findAtivaByPautaId(eq(1L), any(LocalDateTime.class))).thenReturn(Optional.of(sessao));
        when(votoRepository.save(any(Voto.class))).thenReturn(votoSalvo);

        VotarRequest request = new VotarRequest("12345678901", "SIM");
        var response = assertDoesNotThrow(() -> votingService.votar(1L, request));

        assertNotNull(response);
        assertEquals("12345678901", response.getAssociadoId());
        assertEquals("SIM", response.getVoto());
        verify(votoRepository, times(1)).save(any(Voto.class));
    }

    @Test
    @DisplayName("Deve registrar um voto valido com NAO")
    void testVotarValidoNao() {
        Pauta pauta = criarPautaPadrao();
        SessaoVotacao sessao = criarSessaoPadrao(pauta, 60);
        Voto votoSalvo = new Voto(1L, "12345678901", VoteOpcao.NAO);
        votoSalvo.setId(1L);

        when(associadoClient.consultar("12345678901")).thenReturn(StatusVotacao.ABLE_TO_VOTE);
        when(votoRepository.existsByPautaIdAndAssociadoId(1L, "12345678901")).thenReturn(false);
        when(sessaoVotacaoRepository.findAtivaByPautaId(eq(1L), any(LocalDateTime.class))).thenReturn(Optional.of(sessao));
        when(votoRepository.save(any(Voto.class))).thenReturn(votoSalvo);

        VotarRequest request = new VotarRequest("12345678901", "NAO");
        var response = votingService.votar(1L, request);

        assertNotNull(response);
        assertEquals("NAO", response.getVoto());
    }

    @Test
    @DisplayName("Deve lancar CpfInvalidoException quando FakeAssociadoClient retornar CPF_INVALIDO")
    void testVotarCpfInvalido() {
        when(associadoClient.consultar("11111111111")).thenReturn(StatusVotacao.CPF_INVALIDO);
        VotarRequest request = new VotarRequest("11111111111", "SIM");
        assertThrows(CpfInvalidoException.class, () -> votingService.votar(1L, request));
        verify(votoRepository, never()).save(any(Voto.class));
    }

    @Test
    @DisplayName("Deve lancar AssociadoNaoHabilitadoException quando UNABLE_TO_VOTE")
    void testVotarNaoHabilitado() {
        when(associadoClient.consultar("12345678901")).thenReturn(StatusVotacao.UNABLE_TO_VOTE);
        VotarRequest request = new VotarRequest("12345678901", "SIM");
        assertThrows(AssociadoNaoHabilitadoException.class, () -> votingService.votar(1L, request));
        verify(votoRepository, never()).save(any(Voto.class));
    }

    @Test
    @DisplayName("Deve lancar VotacaoDuplicadaException ao tentar votar novamente")
    void testVotarDuplicado() {
        when(associadoClient.consultar("12345678901")).thenReturn(StatusVotacao.ABLE_TO_VOTE);
        when(votoRepository.existsByPautaIdAndAssociadoId(1L, "12345678901")).thenReturn(true);

        VotarRequest request = new VotarRequest("12345678901", "SIM");
        assertThrows(VotacaoDuplicadaException.class, () -> votingService.votar(1L, request));
        verify(votoRepository, never()).save(any(Voto.class));
    }

    @Test
    @DisplayName("Deve lancar SessaoNaoEncontradaException quando nao houver sessao ativa")
    void testVotarSessaoNaoEncontrada() {
        when(associadoClient.consultar("12345678901")).thenReturn(StatusVotacao.ABLE_TO_VOTE);
        when(votoRepository.existsByPautaIdAndAssociadoId(1L, "12345678901")).thenReturn(false);
        when(sessaoVotacaoRepository.findAtivaByPautaId(eq(1L), any(LocalDateTime.class))).thenReturn(Optional.empty());

        VotarRequest request = new VotarRequest("12345678901", "SIM");
        assertThrows(SessaoNaoEncontradaException.class, () -> votingService.votar(1L, request));
    }

    @Test
    @DisplayName("Deve lancar InvalidVoteOptionException ao votar com opcao invalida")
    void testVotarOpcaoInvalida() {
        Pauta pauta = criarPautaPadrao();
        SessaoVotacao sessao = criarSessaoPadrao(pauta, 60);

        when(associadoClient.consultar("12345678901")).thenReturn(StatusVotacao.ABLE_TO_VOTE);
        when(votoRepository.existsByPautaIdAndAssociadoId(1L, "12345678901")).thenReturn(false);
        when(sessaoVotacaoRepository.findAtivaByPautaId(eq(1L), any(LocalDateTime.class))).thenReturn(Optional.of(sessao));

        VotarRequest request = new VotarRequest("12345678901", "TALVEZ");
        assertThrows(InvalidVoteOptionException.class, () -> votingService.votar(1L, request));
    }

    @Test
    @DisplayName("Resultado: Maioria SIM deve retornar APROVADA")
    void testResultadoAprovada() {
        Pauta pauta = criarPautaPadrao();
        when(pautaRepository.findById(1L)).thenReturn(Optional.of(pauta));
        when(votoRepository.countByPautaIdAndVoto(1L, VoteOpcao.SIM)).thenReturn(3L);
        when(votoRepository.countByPautaIdAndVoto(1L, VoteOpcao.NAO)).thenReturn(2L);

        var response = votingService.resultado(1L);

        assertNotNull(response);
        assertEquals(3L, response.getSim());
        assertEquals(2L, response.getNao());
        assertEquals(5L, response.getTotal());
        assertEquals("APROVADA", response.getResultado());
    }

    @Test
    @DisplayName("Resultado: Maioria NAO deve retornar REPROVADA")
    void testResultadoReprovada() {
        Pauta pauta = criarPautaPadrao();
        when(pautaRepository.findById(1L)).thenReturn(Optional.of(pauta));
        when(votoRepository.countByPautaIdAndVoto(1L, VoteOpcao.SIM)).thenReturn(2L);
        when(votoRepository.countByPautaIdAndVoto(1L, VoteOpcao.NAO)).thenReturn(3L);

        var response = votingService.resultado(1L);

        assertEquals(2L, response.getSim());
        assertEquals(3L, response.getNao());
        assertEquals(5L, response.getTotal());
        assertEquals("REPROVADA", response.getResultado());
    }

    @Test
    @DisplayName("Resultado: SIM igual NAO deve retornar EMPATE")
    void testResultadoEmpate() {
        Pauta pauta = criarPautaPadrao();
        when(pautaRepository.findById(1L)).thenReturn(Optional.of(pauta));
        when(votoRepository.countByPautaIdAndVoto(1L, VoteOpcao.SIM)).thenReturn(2L);
        when(votoRepository.countByPautaIdAndVoto(1L, VoteOpcao.NAO)).thenReturn(2L);

        var response = votingService.resultado(1L);

        assertEquals(2L, response.getSim());
        assertEquals(2L, response.getNao());
        assertEquals(4L, response.getTotal());
        assertEquals("EMPATE", response.getResultado());
    }

    @Test
    @DisplayName("Resultado: sem votos deve retornar INDEFINIDA")
    void testResultadoIndefinida() {
        Pauta pauta = criarPautaPadrao();
        when(pautaRepository.findById(1L)).thenReturn(Optional.of(pauta));
        when(votoRepository.countByPautaIdAndVoto(1L, VoteOpcao.SIM)).thenReturn(0L);
        when(votoRepository.countByPautaIdAndVoto(1L, VoteOpcao.NAO)).thenReturn(0L);

        var response = votingService.resultado(1L);

        assertEquals(0L, response.getSim());
        assertEquals(0L, response.getNao());
        assertEquals(0L, response.getTotal());
        assertEquals("INDEFINIDA", response.getResultado());
    }

    @Test
    @DisplayName("Resultado: pauta inexistente deve lancar PautaNaoEncontradaException")
    void testResultadoPautaNaoEncontrada() {
        when(pautaRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(PautaNaoEncontradaException.class, () -> votingService.resultado(99L));
    }

    @Test
    @DisplayName("Deve lancar AssociadoIdInvalidoException se request.associadoId for vazio")
    void testVotarAssociadoIdVazio() {
        VotarRequest request = new VotarRequest("", "SIM");
        assertThrows(AssociadoIdInvalidoException.class, () -> votingService.votar(1L, request));
        verify(associadoClient, never()).consultar(anyString());
    }

    @Test
    @DisplayName("Deve lancar OpcaoVotoInvalidaException se request.voto for vazio")
    void testVotarOpcaoVazia() {
        VotarRequest request = new VotarRequest("12345678901", "");
        assertThrows(OpcaoVotoInvalidaException.class, () -> votingService.votar(1L, request));
        verify(associadoClient, never()).consultar(anyString());
    }
}
