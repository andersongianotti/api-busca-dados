package br.com.example.apibusca.service;

import br.com.example.cep.CepLookup;
import br.com.example.cep.Endereco;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

class CepServiceTest {

    @Test
    void defaultConstructor_shouldCreateService() {
        assertNotNull(new CepService());
    }

    @Test
    void buscarEndereco_shouldDelegateToCepLookup() {
        CepLookup cepLookup = Mockito.mock(CepLookup.class);
        Endereco endereco = new Endereco("01310-100", "Avenida Paulista", null, "Bela Vista", "São Paulo", "SP");
        when(cepLookup.buscarViaCep("01310100")).thenReturn(Optional.of(endereco));

        CepService service = new CepService(cepLookup);
        Optional<Endereco> result = service.buscarEndereco("01310100");

        assertTrue(result.isPresent());
        assertEquals("Avenida Paulista", result.get().logradouro());
    }
}
