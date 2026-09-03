package com.desafio.votacao.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "sessao_votacao")
public class SessaoVotacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "pauta_id", nullable = false)
    private Long pautaId;

    @Column(name = "aberta_em", nullable = false, updatable = false)
    private LocalDateTime abertaEm;

    @Column(name = "encerra_em", nullable = false)
    private LocalDateTime encerraEm;

    // Construtores, getters e setters
    public SessaoVotacao() {
    }

    public SessaoVotacao(Long pautaId, LocalDateTime encerraEm) {
        this();
        this.pautaId = pautaId;
        this.encerraEm = encerraEm;
        this.abertaEm = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPautaId() {
        return pautaId;
    }

    public void setPautaId(Long pautaId) {
        this.pautaId = pautaId;
    }

    public LocalDateTime getAbertaEm() {
        return abertaEm;
    }

    public void setAbertaEm(LocalDateTime abertaEm) {
        this.abertaEm = abertaEm;
    }

    public LocalDateTime getEncerraEm() {
        return encerraEm;
    }

    public void setEncerraEm(LocalDateTime encerraEm) {
        this.encerraEm = encerraEm;
    }
}