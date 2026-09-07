package com.desafio.votacao.integration;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class FakeAssociadoClient implements AssociadoClient {

    @Override
    public StatusVotacao consultar(String cpf) {
        if (cpf == null || cpf.trim().isEmpty() || !isValidCPF(cpf)) {
            return StatusVotacao.CPF_INVALIDO;
        }
        // Simula aleatoriedade determinística baseada no hash do CPF.
        // CPFs distintos produzem o mesmo resultado em chamadas repetidas (idempotência).
        int hash = 0;
        for (int i = 0; i < cpf.length(); i++) {
            hash = (hash * 31) + Character.getNumericValue(cpf.charAt(i));
        }
        boolean ableToVote = Math.abs(hash) % 2 == 0;
        if (ableToVote) {
            return StatusVotacao.ABLE_TO_VOTE;
        } else {
            return StatusVotacao.UNABLE_TO_VOTE;
        }
    }

    private boolean isValidCPF(String cpf) {
        // Validação simplificada de CPF
        if (cpf.length() != 11) {
            return false;
        }
        // Verifica se todos os dígitos são iguais (CPF inválido)
        boolean allSame = true;
        for (int i = 1; i < cpf.length(); i++) {
            if (cpf.charAt(i) != cpf.charAt(0)) {
                allSame = false;
                break;
            }
        }
        if (allSame) {
            return false;
        }
        // Simples validação de dígitos verificadores
        int sum = 0;
        for (int i = 0; i < 9; i++) {
            sum += Character.getNumericValue(cpf.charAt(i)) * (10 - i);
        }
        int remainder = sum % 11;
        if (remainder < 2) {
            if (Character.getNumericValue(cpf.charAt(9)) != 0) {
                return false;
            }
        } else {
            if (Character.getNumericValue(cpf.charAt(9)) != 11 - remainder) {
                return false;
            }
        }
        sum = 0;
        for (int i = 0; i < 10; i++) {
            sum += Character.getNumericValue(cpf.charAt(i)) * (11 - i);
        }
        remainder = sum % 11;
        if (remainder < 2) {
            if (Character.getNumericValue(cpf.charAt(10)) != 0) {
                return false;
            }
        } else {
            if (Character.getNumericValue(cpf.charAt(10)) != 11 - remainder) {
                return false;
            }
        }
        return true;
    }
}