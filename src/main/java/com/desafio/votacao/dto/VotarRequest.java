package com.desafio.votacao.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class VotarRequest {

    @NotNull(message = "ID do associado é obrigatório")
    @Size(max = 255, message = "ID do associado deve ter no máximo 255 caracteres")
    private String associadoId;

    @NotNull(message = "Opção de voto é obrigatória")
    @Size(max = 10, message = "Opção de voto deve ter no máximo 10 caracteres")
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