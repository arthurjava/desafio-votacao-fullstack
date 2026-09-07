package com.desafio.votacao.repository;

import com.desafio.votacao.entity.SessaoVotacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface SessaoVotacaoRepository extends JpaRepository<SessaoVotacao, Long> {

    @Query("SELECT s FROM SessaoVotacao s WHERE s.pautaId = :pautaId AND s.encerraEm > :agora")
    Optional<SessaoVotacao> findAtivaByPautaId(@Param("pautaId") Long pautaId, @Param("agora") LocalDateTime agora);

    Optional<SessaoVotacao> findByPautaId(Long pautaId);
}