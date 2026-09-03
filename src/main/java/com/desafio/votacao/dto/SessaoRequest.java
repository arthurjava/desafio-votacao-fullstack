package com.desafio.votacao.dto;

public class SessaoRequest {
    private Integer duracaoEmSegundos;

    public SessaoRequest() {
    }

    public SessaoRequest(Integer duracaoEmSegundos) {
        this.duracaoEmSegundos = duracaoEmSegundos;
    }

    public Integer getDuracaoEmSegundos() {
        return duracaoEmSegundos;
    }

    public void setDuracaoEmSegundos(Integer duracaoEmSegundos) {
        this.duracaoEmSegundos = duracaoEmSegundos;
    }
}