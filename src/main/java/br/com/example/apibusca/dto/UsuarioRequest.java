package br.com.example.apibusca.dto;

public record UsuarioRequest(
        String name,
        String email,
        String password,
        String phone,
        String cep,
        String logradouro,
        String complemento,
        String bairro,
        String localidade,
        String uf) {
}
