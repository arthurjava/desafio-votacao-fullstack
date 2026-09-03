package com.desafio.votacao.dto;

public class VotarRequest {
    private String associadoId;
    private String voto;

    public VotarRequest() {
    }

    public VotarRequest(String associadoId, String voto) {
        this.associadoId = associadoId;
        this.voto = voto;
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
}