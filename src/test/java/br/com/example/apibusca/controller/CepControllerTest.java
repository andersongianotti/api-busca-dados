package br.com.example.apibusca.controller;

import br.com.example.cep.Endereco;
import br.com.example.apibusca.service.CepService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CepController.class)
class CepControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CepService cepService;

    @Test
    void buscar_shouldReturnEndereco_forValidCep() throws Exception {
        Endereco endereco = new Endereco("01310-100", "Avenida Paulista", null, "Bela Vista", "São Paulo", "SP");
        when(cepService.buscarEndereco("01310100")).thenReturn(Optional.of(endereco));

        mockMvc.perform(get("/api/v1/cep/01310100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.logradouro").value("Avenida Paulista"))
                .andExpect(jsonPath("$.localidade").value("São Paulo"))
                .andExpect(jsonPath("$.uf").value("SP"));
    }

    @Test
    void buscar_shouldReturn404_whenCepNotFound() throws Exception {
        when(cepService.buscarEndereco("99999999")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/cep/99999999"))
                .andExpect(status().isNotFound());
    }
}
