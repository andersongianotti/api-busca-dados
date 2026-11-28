package br.com.example.apibusca.service;

import br.com.example.apibusca.dto.UsuarioRequest;
import br.com.example.apibusca.model.Usuario;
import br.com.example.apibusca.repository.UsuarioRepository;
import br.com.example.cep.Endereco;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    private static final String EMAIL_REGEX = "\\S+@\\S+\\.\\S+";

    private final UsuarioRepository repository;
    private final CepService cepService;
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public UsuarioService(UsuarioRepository repository, CepService cepService) {
        this(repository, cepService, new BCryptPasswordEncoder());
    }

    UsuarioService(UsuarioRepository repository, CepService cepService,
                   BCryptPasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.cepService = cepService;
        this.passwordEncoder = passwordEncoder;
    }

    public List<Usuario> listar() {
        return repository.findAll();
    }

    public Optional<Usuario> buscarPorId(String id) {
        return repository.findById(id);
    }

    public Usuario criar(UsuarioRequest request) {
        validar(request, true);
        if (repository.existsByEmail(request.email().trim())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email já cadastrado");
        }

        Usuario usuario = new Usuario();
        usuario.setName(request.name().trim());
        usuario.setEmail(request.email().trim());
        usuario.setPassword(passwordEncoder.encode(request.password()));
        usuario.setPhone(request.phone().trim());
        aplicarEndereco(usuario, request);
        Instant now = Instant.now();
        usuario.setCreatedAt(now);
        usuario.setUpdatedAt(now);
        return repository.save(usuario);
    }

    public Usuario atualizar(String id, UsuarioRequest request) {
        Usuario usuario = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));

        if (request.name() != null) {
            usuario.setName(request.name().trim());
        }
        if (request.email() != null) {
            String email = request.email().trim();
            repository.findByEmail(email)
                    .filter(outro -> !outro.getId().equals(id))
                    .ifPresent(outro -> {
                        throw new ResponseStatusException(HttpStatus.CONFLICT, "Email já cadastrado");
                    });
            usuario.setEmail(email);
        }
        if (request.password() != null && !request.password().isBlank()) {
            usuario.setPassword(passwordEncoder.encode(request.password()));
        }
        if (request.phone() != null) {
            usuario.setPhone(request.phone().trim());
        }
        if (request.cep() != null && !request.cep().isBlank()) {
            aplicarEndereco(usuario, request);
        }
        usuario.setUpdatedAt(Instant.now());
        return repository.save(usuario);
    }

    public void excluir(String id) {
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado");
        }
        repository.deleteById(id);
    }

    private void validar(UsuarioRequest request, boolean criar) {
        if (request.name() == null || request.name().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nome é obrigatório");
        }
        if (request.email() == null || request.email().isBlank()
                || !request.email().matches(EMAIL_REGEX)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email inválido");
        }
        if (request.phone() == null || request.phone().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Telefone é obrigatório");
        }
        if (request.cep() == null || request.cep().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "CEP é obrigatório");
        }
        if (criar && (request.password() == null || request.password().length() < 6)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Senha é obrigatória e deve ter ao menos 6 caracteres");
        }
    }

    private void aplicarEndereco(Usuario usuario, UsuarioRequest request) {
        if (temEnderecoManual(request)) {
            usuario.setCep(request.cep().replaceAll("\\D", ""));
            usuario.setLogradouro(request.logradouro().trim());
            usuario.setComplemento(blankToNull(request.complemento()));
            usuario.setBairro(blankToNull(request.bairro()));
            usuario.setLocalidade(request.localidade().trim());
            usuario.setUf(request.uf().trim().toUpperCase());
            return;
        }
        Endereco endereco = cepService.buscarEndereco(request.cep())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "CEP não encontrado: " + request.cep()));
        usuario.setCep(endereco.cep());
        usuario.setLogradouro(endereco.logradouro());
        usuario.setComplemento(endereco.complemento());
        usuario.setBairro(endereco.bairro());
        usuario.setLocalidade(endereco.localidade());
        usuario.setUf(endereco.uf());
    }

    private boolean temEnderecoManual(UsuarioRequest request) {
        return request.logradouro() != null && !request.logradouro().isBlank()
                && request.localidade() != null && !request.localidade().isBlank()
                && request.uf() != null && !request.uf().isBlank();
    }

    private String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
