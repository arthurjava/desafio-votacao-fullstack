package com.desafio.votacao.repository;

import com.desafio.votacao.entity.Voto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VotoRepository extends JpaRepository<Voto, Long> {

    @Query("SELECT COUNT(v) FROM Voto v WHERE v.pautaId = :pautaId")
    long countByPautaId(@Param("pautaId") Long pautaId);

    @Query("SELECT COUNT(v) FROM Voto v WHERE v.pautaId = :pautaId")
    long countAllByPautaId(@Param("pautaId") Long pautaId);

    boolean existsByPautaIdAndAssociadoId(Long pautaId, String associadoId);
}