package com.hotel.management.service;

import com.hotel.management.model.Cliente;
import com.hotel.management.repository.ClienteRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class ClienteService {
    private final ClienteRepository repo;

    public ClienteService(ClienteRepository repo) {
        this.repo = repo;
    }

    public List<Cliente> listar() {
        return repo.findAll();
    }

    public Optional<Cliente> buscar(Long id) {
        return repo.findById(id);
    }

    @Transactional
    public Cliente criar(Cliente cliente) {
        cliente.setCriadoEm(Instant.now());
        return repo.save(cliente);
    }

    @Transactional
    public Optional<Cliente> atualizar(Long id, Cliente input) {
        return repo.findById(id).map(c -> {
            c.setNome(input.getNome());
            c.setSobrenome(input.getSobrenome());
            c.setEmail(input.getEmail());
            c.setTelefone(input.getTelefone());
            return repo.save(c);
        });
    }

    @Transactional
    public boolean deletar(Long id) {
        return repo.findById(id).map(c -> {
            repo.delete(c);
            return true;
        }).orElse(false);
    }
}