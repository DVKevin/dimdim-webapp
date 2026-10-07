package br.com.fiap.dimdim.dto;

import java.math.BigDecimal;

public record ContaDTO(
        Long id,
        Long idCliente,
        String nomeCliente,
        String numeroConta,
        String tipoConta,
        BigDecimal saldo,
        String dtAbertura) {
}
