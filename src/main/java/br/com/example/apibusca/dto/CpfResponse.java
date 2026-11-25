package br.com.example.apibusca.dto;

public record CpfResponse(String cpf, String cpfFormatado, boolean valido) {
}
