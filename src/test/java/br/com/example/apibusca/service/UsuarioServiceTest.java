package br.com.example.apibusca.service;

import br.com.example.apibusca.dto.UsuarioRequest;
import br.com.example.apibusca.model.Usuario;
import br.com.example.apibusca.repository.UsuarioRepository;
import br.com.example.cep.Endereco;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository repository;

    @Mock
    private CepService cepService;

    private BCryptPasswordEncoder passwordEncoder;
    private UsuarioService service;

    private static final Endereco ENDERECO =
            new Endereco("01310-100", "Avenida Paulista", "de 1 a 174", "Bela Vista", "São Paulo", "SP");

    @BeforeEach
    void setUp() {
        passwordEncoder = new BCryptPasswordEncoder();
        service = new UsuarioService(repository, cepService, passwordEncoder);
    }

    private UsuarioRequest requestValido() {
        return new UsuarioRequest("Anderson", "anderson@email.com", "senha123", "11999998888", "01310100", null, null, null, null, null);
    }

    private Usuario usuarioSalvo() {
        Usuario u = new Usuario();
        u.setId("abc123");
        u.setName("Anderson");
        u.setEmail("anderson@email.com");
        u.setPassword("hash");
        u.setPhone("11999998888");
        u.setCep("01310-100");
        u.setCreatedAt(java.time.Instant.now());
        return u;
    }

    @Test
    void criar_shouldSaveUserWithHashedPasswordAndResolvedAddress() {
        when(repository.existsByEmail("anderson@email.com")).thenReturn(false);
        when(cepService.buscarEndereco("01310100")).thenReturn(Optional.of(ENDERECO));
        when(repository.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));

        Usuario criado = service.criar(requestValido());

        ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
        verify(repository).save(captor.capture());
        Usuario salvo = captor.getValue();
        assertEquals("Anderson", salvo.getName());
        assertEquals("01310-100", salvo.getCep());
        assertEquals("Avenida Paulista", salvo.getLogradouro());
        assertEquals("SP", salvo.getUf());
        assertNotEquals("senha123", salvo.getPassword());
        assertTrue(passwordEncoder.matches("senha123", salvo.getPassword()));
        assertEquals(criado.getName(), salvo.getName());
    }

    @Test
    void criar_shouldThrow409_whenEmailExists() {
        when(repository.existsByEmail("anderson@email.com")).thenReturn(true);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> service.criar(requestValido()));
        assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
        verify(repository, never()).save(any());
    }

    @Test
    void criar_shouldThrow400_whenCepNotFound() {
        when(repository.existsByEmail("a@b.com")).thenReturn(false);
        when(cepService.buscarEndereco("99999999")).thenReturn(Optional.empty());

        UsuarioRequest req = new UsuarioRequest("Anderson", "a@b.com", "senha123", "11999998888", "99999999", null, null, null, null, null);
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> service.criar(req));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
    }

    @Test
    void criar_shouldThrow400_whenPasswordTooShort() {
        UsuarioRequest req = new UsuarioRequest("Anderson", "a@b.com", "123", "11999998888", "01310100", null, null, null, null, null);
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> service.criar(req));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
    }

    @Test
    void criar_shouldUseManualAddress_whenFieldsProvided() {
        when(repository.existsByEmail("anderson@email.com")).thenReturn(false);
        when(repository.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));

        UsuarioRequest req = new UsuarioRequest("Anderson", "anderson@email.com", "senha123",
                "11999998888", "01310100", "Rua das Flores", "casa 10", "Centro", "Campinas", "sp");
        Usuario criado = service.criar(req);

        assertEquals("Rua das Flores", criado.getLogradouro());
        assertEquals("casa 10", criado.getComplemento());
        assertEquals("Centro", criado.getBairro());
        assertEquals("Campinas", criado.getLocalidade());
        assertEquals("SP", criado.getUf());
        verify(cepService, never()).buscarEndereco(any());
    }

    @Test
    void criar_shouldThrow400_whenEmailInvalid() {
        UsuarioRequest req = new UsuarioRequest("Anderson", "email-invalido", "senha123", "11999998888", "01310100", null, null, null, null, null);
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> service.criar(req));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
    }

    @Test
    void listar_shouldReturnAll() {
        when(repository.findAll()).thenReturn(List.of(usuarioSalvo()));
        assertEquals(1, service.listar().size());
    }

    @Test
    void buscarPorId_shouldReturnUser() {
        when(repository.findById("abc123")).thenReturn(Optional.of(usuarioSalvo()));
        assertTrue(service.buscarPorId("abc123").isPresent());
    }

    @Test
    void atualizar_shouldUpdateFieldsAndAddress() {
        when(repository.findById("abc123")).thenReturn(Optional.of(usuarioSalvo()));
        when(cepService.buscarEndereco("20040002")).thenReturn(Optional.of(
                new Endereco("20040-002", "Rua da Assembleia", null, "Centro", "Rio de Janeiro", "RJ")));
        when(repository.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));

        UsuarioRequest req = new UsuarioRequest("Novo Nome", null, null, "21888887777", "20040002", null, null, null, null, null);
        Usuario atualizado = service.atualizar("abc123", req);

        assertEquals("Novo Nome", atualizado.getName());
        assertEquals("21888887777", atualizado.getPhone());
        assertEquals("RJ", atualizado.getUf());
        assertEquals("anderson@email.com", atualizado.getEmail());
    }

    @Test
    void atualizar_shouldThrow404_whenNotFound() {
        when(repository.findById("x")).thenReturn(Optional.empty());
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> service.atualizar("x", requestValido()));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void atualizar_shouldThrow409_whenEmailTakenByOther() {
        when(repository.findById("abc123")).thenReturn(Optional.of(usuarioSalvo()));
        Usuario outro = usuarioSalvo();
        outro.setId("outro-id");
        outro.setEmail("outro@email.com");
        when(repository.findByEmail("outro@email.com")).thenReturn(Optional.of(outro));

        UsuarioRequest req = new UsuarioRequest(null, "outro@email.com", null, null, null, null, null, null, null, null);
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> service.atualizar("abc123", req));
        assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
    }

    @Test
    void excluir_shouldDelete_whenExists() {
        when(repository.existsById("abc123")).thenReturn(true);
        service.excluir("abc123");
        verify(repository).deleteById("abc123");
    }

    @Test
    void excluir_shouldThrow404_whenNotExists() {
        when(repository.existsById("x")).thenReturn(false);
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> service.excluir("x"));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }
}
