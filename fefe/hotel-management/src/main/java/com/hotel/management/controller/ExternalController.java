package com.hotel.management.controller;

import com.hotel.management.dto.ReservaCreateDTO;
import com.hotel.management.model.Reserva;
import com.hotel.management.service.ReservaService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Endpoint público para integração com site (RF.006).
 * Em produção, proteger via token/API key.
 */
@RestController
@RequestMapping("/external")
public class ExternalController {
    private final ReservaService reservaService;

    public ExternalController(ReservaService reservaService) {
        this.reservaService = reservaService;
    }

    @PostMapping("/reservas")
    public ResponseEntity<?> criarReservaExterna(@Valid @RequestBody ReservaCreateDTO dto) {
        try {
            Reserva r = reservaService.criarReserva(dto.getClienteId(), dto.getQuartoId(), dto.getDataInicio(), dto.getDataFim());
            return ResponseEntity.ok(r);
        } catch (IllegalArgumentException | IllegalStateException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }
}