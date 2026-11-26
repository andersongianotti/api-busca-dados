package br.com.example.apibusca.service;

import br.com.example.cep.CepLookup;
import br.com.example.cep.Endereco;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CepService {

    private final CepLookup cepLookup;

    public CepService() {
        this(new CepLookup());
    }

    CepService(CepLookup cepLookup) {
        this.cepLookup = cepLookup;
    }

    public Optional<Endereco> buscarEndereco(String cep) {
        return cepLookup.buscarViaCep(cep);
    }
}
