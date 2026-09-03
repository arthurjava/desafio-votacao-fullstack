package com.desafio.votacao.service;

import com.desafio.votacao.dto.*;
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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VotingService {

    private final PautaRepository pautaRepository;
    private final SessaoVotacaoRepository sessaoVotacaoRepository;
    private final VotoRepository votoRepository;
    private final AssociadoClient associadoClient;

public VotingService(PautaRepository pautaRepository,
                     SessaoVotacaoRepository sessaoVotacaoRepository,
                     VotoRepository votoRepository,
                     AssociadoClient associadoClient) {
        this.pautaRepository = pautaRepository;
        this.sessaoVotacaoRepository = sessaoVotacaoRepository;
        this.votoRepository = votoRepository;
        this.associadoClient = associadoClient;
    }

    public VotingService() {
        this.pautaRepository = null;
        this.sessaoVotacaoRepository = null;
        this.votoRepository = null;
        this.associadoClient = null;
    }

    public Pauta pesquisarPauta(Long id) {
        return pautaRepository.findById(id)
                .orElseThrow(() -> new PautaNaoEncontradaException(id));
    }

    public PautaResponse criarPauta(CriarPautaRequest request) {
        validarTituloNovo(request.getTitulo());
        Pauta pauta = new Pauta();
        pauta.setTitulo(request.getTitulo());
        pauta.setDescricao(request.getDescricao());
        pauta.setCriadaEm(LocalDateTime.now());
        pauta = pautaRepository.save(pauta);
        return new PautaResponse(pauta.getId(), pauta.getTitulo(), pauta.getDescricao(), pauta.getCriadaEm());
    }

    @Transactional
    public SessaoResponse abrirSessao(Long pautaId, SessaoRequest request) {
        Pauta pauta = pautaRepository.findById(pautaId)
                .orElseThrow(() -> new PautaNaoEncontradaException(pautaId));

        boolean jaTemSessao = sessaoVotacaoRepository.findByPautaId(pautaId).isPresent();
        if (jaTemSessao) {
            throw new SessaoJaExistenteException(pautaId);
        }

        Integer duracao = request.getDuracaoEmSegundos() != null
                ? request.getDuracaoEmSegundos()
                : 60;

        LocalDateTime encerraEm = LocalDateTime.now().plusSeconds(duracao);
        SessaoVotacao sessao = new SessaoVotacao();
        sessao.setPautaId(pautaId);
        sessao.setAbertaEm(LocalDateTime.now());
        sessao.setEncerraEm(encerraEm);
        sessao = sessaoVotacaoRepository.save(sessao);

        return new SessaoResponse(sessao.getId(), sessao.getPautaId(),
                sessao.getAbertaEm(), sessao.getEncerraEm());
    }

    @Transactional
    public VotosRegistrarResponse votar(Long pautaId, VotarRequest request) {
        validarVotoRequest(request);

        String associadoId = request.getAssociadoId();

        // Verificar elegibilidade do associado via integração externa
        StatusVotacao statusVotacao = associadoClient.consultar(associadoId);
        switch (statusVotacao) {
            case CPF_INVALIDO:
                throw new EntidadeNaoEncontradaException("Associado com CPF inválido");
            case UNABLE_TO_VOTE:
                throw new VotacaoDuplicadaException(pautaId, associadoId);
            case ABLE_TO_VOTE:
                // Continuar com o registro do voto
                break;
            default:
                throw new IllegalStateException("Status desconhecido: " + statusVotacao);
        }

        if (votoRepository.existsByPautaIdAndAssociadoId(pautaId, associadoId)) {
            throw new VotacaoDuplicadaException(pautaId, associadoId);
        }

        SessaoVotacao sessao = sessaoVotacaoRepository.findAtivaByPautaId(pautaId)
                .orElseThrow(() -> new SessaoNaoEncontradaException(pautaId));

        if (LocalDateTime.now().isBefore(sessao.getAbertaEm()) ||
                LocalDateTime.now().isAfter(sessao.getEncerraEm())) {
            throw new SessaoEncerradaException(pautaId);
        }

        VoteOpcao votoEnum;
        try {
            votoEnum = VoteOpcao.valueOf(request.getVoto().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidVoteOptionException(request.getVoto());
        }

        Voto voto = new Voto();
        voto.setPautaId(pautaId);
        voto.setAssociadoId(associadoId);
        voto.setVoto(votoEnum);
        voto.setCriadoEm(LocalDateTime.now());
        voto = votoRepository.save(voto);

        return new VotosRegistrarResponse(voto.getId(), voto.getPautaId(),
                voto.getAssociadoId(), voto.getVoto().name(), voto.getCriadoEm());
    }

    public ResultadoResponse resultado(Long pautaId) {
        Pauta pauta = pautaRepository.findById(pautaId)
                .orElseThrow(() -> new PautaNaoEncontradaException(pautaId));

        long sim = votoRepository.countByPautaId(pautaId);
        long total = votoRepository.countAllByPautaId(pautaId);
        long nao = total - sim;

        String resultado = (total > 0 && sim > nao) ? "APROVADA" :
                (total > 0 && nao > sim) ? "REPROVADA" :
                (total > 0 && sim == nao) ? "EMPATE" : "INDEFINIDA";

        return new ResultadoResponse(pautaId, sim, nao, total, resultado);
    }

    private void validarTituloNovo(String titulo) {
        pautaRepository.findByTitulo(titulo).ifPresent(p -> {
            throw new TituloJaExisteException(titulo);
        });
    }

    private void validarVotoRequest(VotarRequest request) {
        if (request.getAssociadoId() == null || request.getAssociadoId().trim().isEmpty()) {
            throw new AssociadoIdInvalidoException("associadoId");
        }
        if (request.getVoto() == null || request.getVoto().trim().isEmpty()) {
            throw new OpcaoVotoInvalidaException("voto");
        }
    }
}