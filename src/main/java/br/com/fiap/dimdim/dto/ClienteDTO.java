package br.com.fiap.dimdim.dto;

public record ClienteDTO(
        Long id,
        String nome,
        String cpf,
        String email,
        String dtCadastro) {
}
