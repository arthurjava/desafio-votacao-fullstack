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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VotingService {

    private static final Logger log = LoggerFactory.getLogger(VotingService.class);

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

    

    /**
     * Busca uma pauta pelo seu ID.
     *
     * @param id identificador da pauta
     * @return objeto Pauta encontrado
     * @throws PautaNaoEncontradaException se a pauta não existir
     */
    public Pauta pesquisarPauta(Long id) {
        return pautaRepository.findById(id)
                .orElseThrow(() -> new PautaNaoEncontradaException(id));
    }

    /**
     * Lista todas as pautas cadastradas.
     *
     * @return lista de pautas
     */
    public List<PautaResponse> listarPautas() {
        return pautaRepository.findAll().stream()
                .map(p -> new PautaResponse(p.getId(), p.getTitulo(), p.getDescricao(), p.getCriadaEm()))
                .toList();
    }

    /**
     * Cria uma nova pauta para votação.
     *
     * @param request dados do pedido de criação da pauta
     * @return resposta contendo os dados da pauta criada
     * @throws TituloJaExisteException se já existir pauta com o mesmo título
     */
    public PautaResponse criarPauta(CriarPautaRequest request) {
        validarTituloNovo(request.getTitulo());
        Pauta pauta = new Pauta();
        pauta.setTitulo(request.getTitulo());
        pauta.setDescricao(request.getDescricao());
        pauta.setCriadaEm(LocalDateTime.now());
        pauta = pautaRepository.save(pauta);
        log.info("Pauta criada: id={}, titulo='{}'", pauta.getId(), pauta.getTitulo());
        return new PautaResponse(pauta.getId(), pauta.getTitulo(), pauta.getDescricao(), pauta.getCriadaEm());
    }

    /**
     * Abre uma sessão de votação para a pauta especificada.
     *
     * @param pautaId identificador da pauta
     * @param request dados da sessão de votação (duracaoEmSegundos é opcional, usa 60s por padrão)
     * @return resposta contendo os dados da sessão aberta
     * @throws PautaNaoEncontradaException se a pauta não existir
     * @throws SessaoJaExistenteException se já existir sessão para esta pauta
     */
    @Transactional
    public SessaoResponse abrirSessao(Long pautaId, SessaoRequest request) {
        Pauta pauta = pautaRepository.findById(pautaId)
                .orElseThrow(() -> new PautaNaoEncontradaException(pautaId));

        boolean jaTemSessao = sessaoVotacaoRepository.findByPautaId(pautaId).isPresent();
        if (jaTemSessao) {
            throw new SessaoJaExistenteException(pautaId);
        }

        SessaoRequest req = request != null ? request : new SessaoRequest();
        Integer duracao = Objects.requireNonNullElse(req.getDuracaoEmSegundos(), 60);

        LocalDateTime encerraEm = LocalDateTime.now().plusSeconds(duracao);
        SessaoVotacao sessao = new SessaoVotacao();
        sessao.setPautaId(pautaId);
        sessao.setAbertaEm(LocalDateTime.now());
        sessao.setEncerraEm(encerraEm);
        sessao = sessaoVotacaoRepository.save(sessao);

        log.info("Sessão de votação aberta: pautaId={}, sessaoId={}, duracao={}s", pautaId, sessao.getId(), duracao);
        return new SessaoResponse(sessao.getId(), sessao.getPautaId(),
                sessao.getAbertaEm(), sessao.getEncerraEm());
    }

    /**
     * Registra o voto de um associado em uma pauta.
     *
     * @param pautaId identificador da pauta
     * @param request dados do voto (associadoId e opcao de voto)
     * @return resposta contendo os dados do voto registrado
     * @throws CpfInvalidoException se o CPF do associado for inválido
     * @throws VotacaoDuplicadaException se o associado já tiver votado nesta pauta
     * @throws SessaoNaoEncontradaException se a sessão de votação não for encontrada ou estiver encerrada
     * @throws InvalidVoteOptionException se a opção de voto for inválida
     * @throws AssociadoIdInvalidoException se o ID do associado for inválido ou ausente
     */
    @Transactional
    public VotosRegistrarResponse votar(Long pautaId, VotarRequest request) {
        validarVotoRequest(request);

        String associadoId = request.getAssociadoId();

        // Verificar elegibilidade do associado via integração externa
        StatusVotacao statusVotacao = associadoClient.consultar(associadoId);
        switch (statusVotacao) {
            case CPF_INVALIDO:
                throw new CpfInvalidoException(associadoId);
            case UNABLE_TO_VOTE:
                throw new AssociadoNaoHabilitadoException(associadoId);
            case ABLE_TO_VOTE:
                // Continuar com o registro do voto
                break;
            default:
                throw new IllegalStateException("Status desconhecido: " + statusVotacao);
        }

        if (votoRepository.existsByPautaIdAndAssociadoId(pautaId, associadoId)) {
            throw new VotacaoDuplicadaException(pautaId, associadoId);
        }

        LocalDateTime agora = LocalDateTime.now();
        SessaoVotacao sessao = sessaoVotacaoRepository.findAtivaByPautaId(pautaId, agora)
                .orElseThrow(() -> new SessaoNaoEncontradaException(pautaId));

        if (agora.isBefore(sessao.getAbertaEm()) ||
                agora.isAfter(sessao.getEncerraEm())) {
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

        log.info("Voto registrado: pautaId={}, associadoId={}, voto={}", pautaId, associadoId, votoEnum);
        return new VotosRegistrarResponse(voto.getId(), voto.getPautaId(),
                voto.getAssociadoId(), voto.getVoto().name(), voto.getCriadoEm());
    }

    /**
     * Obtém o resultado da votação para uma pauta.
     *
     * @param pautaId identificador da pauta
     * @return resposta contendo o total de votos SIM, NAO e o resultado final
     * @throws PautaNaoEncontradaException se a pauta não existir
     */
    public ResultadoResponse resultado(Long pautaId) {
        Pauta pauta = pautaRepository.findById(pautaId)
                .orElseThrow(() -> new PautaNaoEncontradaException(pautaId));

        long sim = votoRepository.countByPautaIdAndVoto(pautaId, Voto.VoteOpcao.SIM);
        long nao = votoRepository.countByPautaIdAndVoto(pautaId, Voto.VoteOpcao.NAO);
        long total = sim + nao;

        String resultado;
        if (total == 0) {
            resultado = "INDEFINIDA";
        } else if (sim > nao) {
            resultado = "APROVADA";
        } else if (nao > sim) {
            resultado = "REPROVADA";
        } else {
            resultado = "EMPATE";
        }

        log.info("Resultado da votação: pautaId={}, sim={}, nao={}, total={}, resultado={}", pautaId, sim, nao, total, resultado);
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