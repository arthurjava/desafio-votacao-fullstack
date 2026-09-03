package com.desafio.votacao.dto;

import java.time.LocalDateTime;

public class VotersRegistrarResponse {
    private Long id;
    private Long pautaId;
    private String associadoId;
    private String voto;
    private LocalDateTime criadoEm;

    public VotersRegistrarResponse() {
    }

    public VotersRegistrarResponse(Long id, Long pautaId, String associadoId, String voto, LocalDateTime criadoEm) {
        this.id = id;
        this.pautaId = pautaId;
        this.associadoId = associadoId;
        this.voto = voto;
        this.criadoEm = criadoEm;
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

    public String getVoto() {
        return voto;
    }

    public void setVoto(String voto) {
        this.voto = voto;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(LocalDateTime criadoEm) {
        this.criadoEm = criadoEm;
    }
}