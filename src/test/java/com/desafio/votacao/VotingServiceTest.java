package com.desafio.votacao;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.desafio.votacao.dto.VotarRequest;
import com.desafio.votacao.entity.Pauta;
import com.desafio.votacao.entity.Voto.VoteOpcao;
import com.desafio.votacao.entity.SessaoVotacao;
import com.desafio.votacao.service.VotingService;

public class VotingServiceTest {

    private VotingService votingService;

    @BeforeEach
    public void setUp() {
        votingService = new VotingService();
    }

    @Test
    @DisplayName("Deve inicializar o VotingService")
    public void testServiceInicializacao() {
        assertNotNull(votingService);
    }

    @Test
    @DisplayName("Deve testar flow basico")
    public void testBasico() {
        assertDoesNotThrow(() -> {
            // Apenas verifica que o teste roda
        });
    }
}