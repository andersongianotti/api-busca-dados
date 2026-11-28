package br.com.example.apibusca.dto;

public record EnderecoResponse(
        String cep,
        String logradouro,
        String complemento,
        String bairro,
        String localidade,
        String uf) {

    public static EnderecoResponse from(br.com.example.cep.Endereco endereco) {
        return new EnderecoResponse(
                endereco.cep(),
                endereco.logradouro(),
                endereco.complemento(),
                endereco.bairro(),
                endereco.localidade(),
                endereco.uf());
    }
}
