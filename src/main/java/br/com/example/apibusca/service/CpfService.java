package br.com.example.apibusca.service;

import br.com.example.apibusca.dto.CpfResponse;
import br.com.example.cpf.CpfValidator;
import org.springframework.stereotype.Service;

@Service
public class CpfService {

    public CpfResponse validate(String cpf) {
        return new CpfResponse(cpf, CpfValidator.format(cpf), CpfValidator.isValid(cpf));
    }
}
