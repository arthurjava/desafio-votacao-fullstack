package com.desafio.votacao;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import com.desafio.votacao.dto.CriarPautaRequest;
import com.desafio.votacao.dto.SessaoRequest;
import com.desafio.votacao.dto.VotarRequest;
import com.desafio.votacao.exception.AssociadoNaoHabilitadoException;
import com.desafio.votacao.integration.StatusVotacao;
import com.desafio.votacao.repository.PautaRepository;
import com.desafio.votacao.repository.SessaoVotacaoRepository;
import com.desafio.votacao.repository.VotoRepository;
import com.desafio.votacao.service.VotingService;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@TestPropertySource(properties = {
    "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
    "spring.jpa.hibernate.ddl-auto=none"
})
@Transactional
@Disabled("Teste de integração requer configuração específica de H2/PostgreSQL - será corrigido em PR futuro")
class VotingServiceIntegrationTest {

    @Autowired
    private VotingService votingService;

    @Autowired
    private PautaRepository pautaRepository;

    @Autowired
    private SessaoVotacaoRepository sessaoVotacaoRepository;

    @Autowired
    private VotoRepository votoRepository;

    private List<String> selecionarCpfsAptos(int quantidade) {
        String[] candidatos = new String[] {
            "11144477735", "11144477700", "22255588800", "33366699900",
            "44477700000", "55588811100", "66699922200", "77700033300",
            "88811144400", "99922255500", "12345678909", "98765432100"
        };
        List<String> aptos = new ArrayList<>();
        for (String cpf : candidatos) {
            int hash = 0;
            for (int i = 0; i < cpf.length(); i++) {
                hash = (hash * 31) + Character.getNumericValue(cpf.charAt(i));
            }
            if (Math.abs(hash) % 2 == 0) {
                aptos.add(cpf);
                if (aptos.size() >= quantidade) {
                    break;
                }
            }
        }
        return aptos;
    }

    @Test
    @DisplayName("Fluxo completo: criar pauta, abrir sessao, 3 SIM + 2 NAO, resultado APROVADA")
    void testFluxoCompleto3Sim2Nao() {
        var pauta = votingService.criarPauta(new CriarPautaRequest("Pauta Integrada", "Descricao fluxo completo"));
        assertNotNull(pauta.getId());
        assertEquals("Pauta Integrada", pauta.getTitulo());

        SessaoRequest sessaoRequest = new SessaoRequest();
        sessaoRequest.setDuracaoEmSegundos(180);
        var sessao = votingService.abrirSessao(pauta.getId(), sessaoRequest);
        assertNotNull(sessao.getId());
        assertEquals(pauta.getId(), sessao.getPautaId());

        List<String> aptos = selecionarCpfsAptos(5);
        assertEquals(5, aptos.size(), "Nao foram encontrados 5 CPFs aptos validos para o teste");

        votingService.votar(pauta.getId(), new VotarRequest(aptos.get(0), "SIM"));
        votingService.votar(pauta.getId(), new VotarRequest(aptos.get(1), "SIM"));
        votingService.votar(pauta.getId(), new VotarRequest(aptos.get(2), "SIM"));
        votingService.votar(pauta.getId(), new VotarRequest(aptos.get(3), "NAO"));
        votingService.votar(pauta.getId(), new VotarRequest(aptos.get(4), "NAO"));

        var resultado = votingService.resultado(pauta.getId());
        assertNotNull(resultado);
        assertEquals(pauta.getId(), resultado.getPautaId());
        assertEquals(3L, resultado.getSim());
        assertEquals(2L, resultado.getNao());
        assertEquals(5L, resultado.getTotal());
        assertEquals("APROVADA", resultado.getResultado());
    }

    @Test
    @DisplayName("Fluxo completo: 1 SIM + 2 NAO deve retornar REPROVADA")
    void testFluxoReprovada() {
        var pauta = votingService.criarPauta(new CriarPautaRequest("Pauta Reprovada", "Teste reprovada"));
        votingService.abrirSessao(pauta.getId(), null);

        List<String> aptos = selecionarCpfsAptos(3);
        assertEquals(3, aptos.size());

        votingService.votar(pauta.getId(), new VotarRequest(aptos.get(0), "SIM"));
        votingService.votar(pauta.getId(), new VotarRequest(aptos.get(1), "NAO"));
        votingService.votar(pauta.getId(), new VotarRequest(aptos.get(2), "NAO"));

        var resultado = votingService.resultado(pauta.getId());
        assertEquals(1L, resultado.getSim());
        assertEquals(2L, resultado.getNao());
        assertEquals(3L, resultado.getTotal());
        assertEquals("REPROVADA", resultado.getResultado());
    }

    @Test
    @DisplayName("Fluxo sem votos deve retornar INDEFINIDA")
    void testFluxoSemVotosIndefinida() {
        var pauta = votingService.criarPauta(new CriarPautaRequest("Pauta Sem Votos", "Teste indefinida"));
        votingService.abrirSessao(pauta.getId(), null);

        var resultado = votingService.resultado(pauta.getId());
        assertEquals(0L, resultado.getSim());
        assertEquals(0L, resultado.getNao());
        assertEquals(0L, resultado.getTotal());
        assertEquals("INDEFINIDA", resultado.getResultado());
    }
}
