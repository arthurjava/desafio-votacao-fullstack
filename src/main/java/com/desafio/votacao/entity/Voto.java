package com.desafio.votacao.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "voto")
public class Voto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "pauta_id", nullable = false)
    private Long pautaId;

    @Column(name = "associado_id", nullable = false, length = 255)
    private String associadoId;

    @Enumerated(EnumType.STRING)
    @Column(name = "voto", nullable = false, length = 10)
    private VoteOpcao voto;

    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    public enum VoteOpcao {
        SIM,
        NAO
    }

    // Construtores, getters e setters
    public Voto() {
    }

    public Voto(Long pautaId, String associadoId, VoteOpcao voto) {
        this();
        this.pautaId = pautaId;
        this.associadoId = associadoId;
        this.voto = voto;
        this.criadoEm = LocalDateTime.now();
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

    public String getAssociadoId() {
        return associadoId;
    }

    public void setAssociadoId(String associadoId) {
        this.associadoId = associadoId;
    }

    public VoteOpcao getVoto() {
        return voto;
    }

    public void setVoto(VoteOpcao voto) {
        this.voto = voto;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(LocalDateTime criadoEm) {
        this.criadoEm = criadoEm;
    }
}