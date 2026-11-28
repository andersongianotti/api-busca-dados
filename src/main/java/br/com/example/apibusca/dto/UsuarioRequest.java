package br.com.example.apibusca.dto;

public record UsuarioRequest(
        String name,
        String email,
        String password,
        String phone,
        String cep) {
}
