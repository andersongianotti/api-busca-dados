package br.com.example.apibusca;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.boot.SpringApplication;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockStatic;

class ApiBuscaDadosApplicationTest {

    @Test
    void main_shouldDelegateToSpringApplication() {
        try (MockedStatic<SpringApplication> mocked = mockStatic(SpringApplication.class)) {
            ApiBuscaDadosApplication.main(new String[]{});

            mocked.verify(() -> SpringApplication.run(
                    eq(ApiBuscaDadosApplication.class), any(String[].class)));
        }
    }
}
