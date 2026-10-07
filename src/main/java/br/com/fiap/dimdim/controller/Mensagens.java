package br.com.fiap.dimdim.controller;

import org.springframework.dao.DataIntegrityViolationException;

final class Mensagens {

    private Mensagens() {
    }

    static String de(Exception e) {
        if (e instanceof DataIntegrityViolationException) {
            return "Operacao recusada pelo banco: registro duplicado ou em uso por outra tabela.";
        }
        return e.getMessage() != null ? e.getMessage() : "Erro inesperado.";
    }
}
