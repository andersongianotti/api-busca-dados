package br.com.example.apibusca.controller;

import br.com.example.apibusca.dto.CpfRequest;
import br.com.example.apibusca.dto.CpfResponse;
import br.com.example.apibusca.service.CpfService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/cpf")
public class CpfController {

    private final CpfService cpfService;

    public CpfController(CpfService cpfService) {
        this.cpfService = cpfService;
    }

    @PostMapping("/validar")
    public ResponseEntity<CpfResponse> validar(@RequestBody CpfRequest request) {
        return ResponseEntity.ok(cpfService.validate(request.cpf()));
    }

    @GetMapping("/{cpf}/valido")
    public ResponseEntity<CpfResponse> validarPath(@PathVariable String cpf) {
        return ResponseEntity.ok(cpfService.validate(cpf));
    }
}
