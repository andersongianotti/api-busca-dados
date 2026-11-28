package br.com.example.apibusca.controller;

import br.com.example.apibusca.dto.UsuarioRequest;
import br.com.example.apibusca.dto.UsuarioResponse;
import br.com.example.apibusca.model.Usuario;
import br.com.example.apibusca.service.UsuarioService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UsuarioController.class)
class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UsuarioService usuarioService;

    private Usuario usuario() {
        Usuario u = new Usuario();
        u.setId("abc123");
        u.setName("Anderson");
        u.setEmail("anderson@email.com");
        u.setPhone("11999998888");
        u.setCep("01310-100");
        u.setLogradouro("Avenida Paulista");
        u.setBairro("Bela Vista");
        u.setLocalidade("São Paulo");
        u.setUf("SP");
        u.setCreatedAt(Instant.parse("2026-01-01T00:00:00Z"));
        return u;
    }

    @Test
    void listar_shouldReturnUsers() throws Exception {
        when(usuarioService.listar()).thenReturn(List.of(usuario()));

        mockMvc.perform(get("/api/v1/usuarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Anderson"))
                .andExpect(jsonPath("$[0].email").value("anderson@email.com"))
                .andExpect(jsonPath("$[0].endereco").value(org.hamcrest.Matchers.containsString("Avenida Paulista")));
    }

    @Test
    void buscar_shouldReturnUser() throws Exception {
        when(usuarioService.buscarPorId("abc123")).thenReturn(Optional.of(usuario()));

        mockMvc.perform(get("/api/v1/usuarios/abc123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("abc123"))
                .andExpect(jsonPath("$.uf").value("SP"));
    }

    @Test
    void buscar_shouldReturn404_whenMissing() throws Exception {
        when(usuarioService.buscarPorId("x")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/usuarios/x"))
                .andExpect(status().isNotFound());
    }

    @Test
    void criar_shouldReturn201() throws Exception {
        when(usuarioService.criar(any(UsuarioRequest.class))).thenReturn(usuario());

        String body = """
                {"name":"Anderson","email":"anderson@email.com","password":"senha123","phone":"11999998888","cep":"01310100"}
                """;
        mockMvc.perform(post("/api/v1/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Anderson"))
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    void atualizar_shouldReturnUpdated() throws Exception {
        when(usuarioService.atualizar(eq("abc123"), any(UsuarioRequest.class))).thenReturn(usuario());

        String body = """
                {"name":"Novo Nome","email":null,"password":null,"phone":"21888887777","cep":null}
                """;
        mockMvc.perform(put("/api/v1/usuarios/abc123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Anderson"));
    }

    @Test
    void excluir_shouldReturn204() throws Exception {
        mockMvc.perform(delete("/api/v1/usuarios/abc123"))
                .andExpect(status().isNoContent());
        verify(usuarioService).excluir("abc123");
    }
}
