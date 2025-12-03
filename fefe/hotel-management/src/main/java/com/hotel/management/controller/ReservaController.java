package com.hotel.management.controller;

import com.hotel.management.dto.ReservaCreateDTO;
import com.hotel.management.model.Reserva;
import com.hotel.management.model.Servico;
import com.hotel.management.service.ReservaService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reservas")
public class ReservaController {
    private final ReservaService service;

    public ReservaController(ReservaService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<?> criar(@Valid @RequestBody ReservaCreateDTO dto) {
        try {
            Reserva criada = service.criarReserva(dto.getClienteId(), dto.getQuartoId(), dto.getDataInicio(), dto.getDataFim());
            return ResponseEntity.ok(criada);
        } catch (IllegalArgumentException | IllegalStateException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscar(@PathVariable Long id) {
        return service.buscar(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/checkin")
    public ResponseEntity<?> checkIn(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(service.checkIn(id));
        } catch (IllegalStateException | IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @PostMapping("/{id}/checkout")
    public ResponseEntity<?> checkOut(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(service.checkOut(id));
        } catch (IllegalStateException | IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @PostMapping("/{id}/servicos")
    public ResponseEntity<?> adicionarServico(@PathVariable Long id, @RequestBody Servico servico) {
        try {
            var salvo = service.adicionarServico(id, servico);
            return ResponseEntity.ok(salvo);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }
}