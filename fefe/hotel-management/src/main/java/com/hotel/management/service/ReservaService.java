package com.hotel.management.service;

import com.hotel.management.model.*;
import com.hotel.management.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class ReservaService {
    private final ReservaRepository reservaRepo;
    private final ClienteRepository clienteRepo;
    private final QuartoRepository quartoRepo;
    private final ServicoRepository servicoRepo;
    private final FaturaRepository faturaRepo;

    public ReservaService(ReservaRepository reservaRepo,
                          ClienteRepository clienteRepo,
                          QuartoRepository quartoRepo,
                          ServicoRepository servicoRepo,
                          FaturaRepository faturaRepo) {
        this.reservaRepo = reservaRepo;
        this.clienteRepo = clienteRepo;
        this.quartoRepo = quartoRepo;
        this.servicoRepo = servicoRepo;
        this.faturaRepo = faturaRepo;
    }

    /**
     * Cria reserva validando existência de cliente/quarto e evitando sobreposições.
     */
    @Transactional
    public Reserva criarReserva(Long clienteId, Long quartoId, LocalDate inicio, LocalDate fim) {
        Assert.isTrue(inicio.isBefore(fim) || inicio.isEqual(fim), "Data de início deve ser anterior ou igual à data fim");

        Cliente cliente = clienteRepo.findById(clienteId).orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado"));
        Quarto quarto = quartoRepo.findById(quartoId).orElseThrow(() -> new IllegalArgumentException("Quarto não encontrado"));

        List<Reserva> overlaps = reservaRepo.findReservasSobrepostas(quartoId, inicio, fim);
        if (!overlaps.isEmpty()) {
            throw new IllegalStateException("Quarto já reservado para o período informado");
        }

        Reserva r = Reserva.builder()
                .cliente(cliente)
                .quarto(quarto)
                .dataInicio(inicio)
                .dataFim(fim)
                .status(Reserva.StatusReserva.RESERVADO)
                .build();

        // salvar reserva
        return reservaRepo.save(r);
    }

    public Optional<Reserva> buscar(Long id) {
        return reservaRepo.findById(id);
    }

    @Transactional
    public Reserva checkIn(Long reservaId) {
        Reserva r = reservaRepo.findById(reservaId).orElseThrow(() -> new IllegalArgumentException("Reserva não encontrada"));
        if (r.getStatus() != Reserva.StatusReserva.RESERVADO) {
            throw new IllegalStateException("Reserva não está em estado RESERVADO");
        }
        r.setStatus(Reserva.StatusReserva.CHECKED_IN);
        Quarto q = r.getQuarto();
        q.setStatus(Quarto.Status.OCUPADO);
        quartoRepo.save(q);
        return reservaRepo.save(r);
    }

    @Transactional
    public Reserva checkOut(Long reservaId) {
        Reserva r = reservaRepo.findById(reservaId).orElseThrow(() -> new IllegalArgumentException("Reserva não encontrada"));
        if (r.getStatus() != Reserva.StatusReserva.CHECKED_IN) {
            throw new IllegalStateException("Reserva não está em estado CHECKED_IN");
        }
        r.setStatus(Reserva.StatusReserva.CHECKED_OUT);
        Quarto q = r.getQuarto();
        q.setStatus(Quarto.Status.DISPONIVEL);
        quartoRepo.save(q);

        // gerar fatura simples (hospedagem + serviços)
        BigDecimal total = BigDecimal.ZERO;
        long nights = Math.max(1, java.time.temporal.ChronoUnit.DAYS.between(r.getDataInicio(), r.getDataFim()));
        if (r.getQuarto().getPrecoPorNoite() != null) {
            total = total.add(r.getQuarto().getPrecoPorNoite().multiply(BigDecimal.valueOf(nights)));
        }
        if (r.getServicos() != null) {
            for (Servico s : r.getServicos()) {
                if (s.getValor() != null) total = total.add(s.getValor());
            }
        }

        Fatura f = Fatura.builder()
                .reserva(r)
                .total(total)
                .emitidaEm(Instant.now())
                .paga(false)
                .build();

        r.setFatura(f);
        faturaRepo.save(f);
        return reservaRepo.save(r);
    }

    @Transactional
    public Servico adicionarServico(Long reservaId, Servico servico) {
        Reserva r = reservaRepo.findById(reservaId).orElseThrow(() -> new IllegalArgumentException("Reserva não encontrada"));
        servico.setReserva(r);
        Servico salvo = servicoRepo.save(servico);
        r.getServicos().add(salvo);
        reservaRepo.save(r);
        return salvo;
    }
}