package br.com.example.apibusca.service;

import br.com.example.apibusca.dto.CpfResponse;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CpfServiceTest {

    private final CpfService service = new CpfService();

    @Test
    void validate_shouldReturnValidResponse_forValidCpf() {
        CpfResponse response = service.validate("529.982.247-25");

        assertTrue(response.valido());
        assertEquals("529.982.247-25", response.cpf());
        assertEquals("529.982.247-25", response.cpfFormatado());
    }

    @Test
    void validate_shouldReturnInvalidResponse_forInvalidCpf() {
        CpfResponse response = service.validate("11111111111");

        assertFalse(response.valido());
        assertEquals("111.111.111-11", response.cpfFormatado());
    }
}
