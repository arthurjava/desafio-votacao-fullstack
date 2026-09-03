package com.desafio.votacao.dto;

public class ResultadoResponse {
    private Long pautaId;
    private long sim;
    private long nao;
    private long total;
    private String resultado;

    public ResultadoResponse() {
    }

    public ResultadoResponse(Long pautaId, long sim, long nao, long total, String resultado) {
        this.pautaId = pautaId;
        this.sim = sim;
        this.nao = nao;
        this.total = total;
        this.resultado = resultado;
    }

    public Long getPautaId() {
        return pautaId;
    }

    public void setPautaId(Long pautaId) {
        this.pautaId = pautaId;
    }

    public long getSim() {
        return sim;
    }

    public void setSim(long sim) {
        this.sim = sim;
    }

    public long getNao() {
        return nao;
    }

    public void setNao(long nao) {
        this.nao = nao;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public String getResultado() {
        return resultado;
    }

    public void setResultado(String resultado) {
        this.resultado = resultado;
    }
}