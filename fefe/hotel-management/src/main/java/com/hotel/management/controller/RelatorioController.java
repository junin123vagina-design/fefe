package com.hotel.management.controller;

import com.hotel.management.service.RelatorioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/relatorios")
public class RelatorioController {
    private final RelatorioService service;

    public RelatorioController(RelatorioService service) {
        this.service = service;
    }

    @GetMapping("/mensal")
    public ResponseEntity<?> relatorioMensal(@RequestParam int ano, @RequestParam int mes) {
        var rel = service.gerarRelatorioMensal(ano, mes);
        return ResponseEntity.ok(rel);
    }
}