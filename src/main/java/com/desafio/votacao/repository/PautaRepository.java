package com.desafio.votacao.repository;

import com.desafio.votacao.entity.Pauta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PautaRepository extends JpaRepository<Pauta, Long> {

    @Query("SELECT p FROM Pauta p WHERE p.titulo = :titulo")
    Optional<Pauta> findByTitulo(@Param("titulo") String titulo);
}