package br.com.fiap.dimdim.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

final class Validacao {

    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private Validacao() {
    }

    static String obrigatorio(String valor, String campo, int max) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException("O campo '" + campo + "' e obrigatorio");
        }
        String limpo = valor.trim();
        if (limpo.length() > max) {
            throw new IllegalArgumentException("O campo '" + campo + "' aceita no maximo " + max + " caracteres");
        }
        return limpo;
    }

    static String data(LocalDateTime data) {
        return data == null ? null : data.format(FORMATO);
    }
}
