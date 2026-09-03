package com.desafio.votacao.dto;

import java.time.LocalDateTime;

public class SessaoResponse {
    private Long id;
    private Long pautaId;
    private LocalDateTime abertaEm;
    private LocalDateTime encerraEm;

    public SessaoResponse() {
    }

    public SessaoResponse(Long id, Long pautaId, LocalDateTime abertaEm, LocalDateTime encerraEm) {
        this.id = id;
        this.pautaId = pautaId;
        this.abertaEm = abertaEm;
        this.encerraEm = encerraEm;
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