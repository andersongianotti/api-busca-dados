package br.com.example.apibusca.dto;

import br.com.example.apibusca.model.Usuario;

import java.time.Instant;

public record UsuarioResponse(
        String id,
        String name,
        String email,
        String phone,
        String cep,
        String logradouro,
        String complemento,
        String bairro,
        String localidade,
        String uf,
        String endereco,
        Instant createdAt,
        Instant updatedAt) {

    public static UsuarioResponse from(Usuario usuario) {
        return new UsuarioResponse(
                usuario.getId(),
                usuario.getName(),
                usuario.getEmail(),
                usuario.getPhone(),
                usuario.getCep(),
                usuario.getLogradouro(),
                usuario.getComplemento(),
                usuario.getBairro(),
                usuario.getLocalidade(),
                usuario.getUf(),
                formatarEndereco(usuario),
                usuario.getCreatedAt(),
                usuario.getUpdatedAt());
    }

    private static String formatarEndereco(Usuario usuario) {
        if (usuario.getCep() == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        if (usuario.getLogradouro() != null) {
            sb.append(usuario.getLogradouro());
        }
        if (usuario.getComplemento() != null && !usuario.getComplemento().isBlank()) {
            sb.append(", ").append(usuario.getComplemento());
        }
        if (usuario.getBairro() != null) {
            sb.append(" - ").append(usuario.getBairro());
        }
        if (usuario.getLocalidade() != null) {
            sb.append(", ").append(usuario.getLocalidade());
        }
        if (usuario.getUf() != null) {
            sb.append("/").append(usuario.getUf());
        }
        sb.append(" - CEP ").append(usuario.getCep());
        return sb.toString();
    }
}
