package br.com.example.apibusca.controller;

import br.com.example.apibusca.dto.CpfResponse;
import br.com.example.apibusca.service.CpfService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CpfController.class)
class CpfControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CpfService cpfService;

    @Test
    void postValidar_shouldReturnResponse_forValidCpf() throws Exception {
        when(cpfService.validate(anyString()))
                .thenReturn(new CpfResponse("529.982.247-25", "529.982.247-25", true));

        mockMvc.perform(post("/api/v1/cpf/validar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"cpf\":\"529.982.247-25\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valido").value(true))
                .andExpect(jsonPath("$.cpfFormatado").value("529.982.247-25"));
    }

    @Test
    void postValidar_shouldReturnInvalid_forInvalidCpf() throws Exception {
        when(cpfService.validate(anyString()))
                .thenReturn(new CpfResponse("11111111111", "111.111.111-11", false));

        mockMvc.perform(post("/api/v1/cpf/validar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"cpf\":\"11111111111\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valido").value(false));
    }

    @Test
    void getValido_shouldReturnResponse_forValidCpf() throws Exception {
        when(cpfService.validate("52998224725"))
                .thenReturn(new CpfResponse("52998224725", "529.982.247-25", true));

        mockMvc.perform(get("/api/v1/cpf/52998224725/valido"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valido").value(true));
    }
}
