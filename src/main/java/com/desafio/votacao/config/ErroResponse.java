package com.desafio.votacao.config;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ErroResponse {
    private final int status;
    private final String mensagem;
    private final String timestamp;
    private final String path;

    public ErroResponse(int status, String mensagem, String timestamp, String path) {
        this.status = status;
        this.mensagem = mensagem;
        this.timestamp = timestamp;
        this.path = path;
    }
}