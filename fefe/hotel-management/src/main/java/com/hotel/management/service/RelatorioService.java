package com.hotel.management.service;

import com.hotel.management.model.Fatura;
import com.hotel.management.model.Reserva;
import com.hotel.management.repository.FaturaRepository;
import com.hotel.management.repository.ReservaRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Service
public class RelatorioService {
    private final ReservaRepository reservaRepo;
    private final FaturaRepository faturaRepo;

    public RelatorioService(ReservaRepository reservaRepo, FaturaRepository faturaRepo) {
        this.reservaRepo = reservaRepo;
        this.faturaRepo = faturaRepo;
    }

    public RelatorioMensal gerarRelatorioMensal(int ano, int mes) {
        YearMonth ym = YearMonth.of(ano, mes);
        LocalDate inicio = ym.atDay(1);
        LocalDate fim = ym.atEndOfMonth();

        List<Reserva> reservas = reservaRepo.findReservasNoPeriodo(inicio, fim);

        long ocupadas = reservas.stream()
                .filter(r -> r.getStatus() == Reserva.StatusReserva.CHECKED_IN || r.getStatus() == Reserva.StatusReserva.CHECKED_OUT)
                .count();

        BigDecimal faturamento = faturaRepo.findAll().stream()
                .filter(f -> {
                    return f.getEmitidaEm() != null && YearMonth.from(f.getEmitidaEm().atZone(java.time.ZoneId.systemDefault()).toLocalDate()).equals(ym);
                })
                .map(Fatura::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long servicosCount = reservas.stream().mapToLong(r -> r.getServicos() != null ? r.getServicos().size() : 0).sum();

        return new RelatorioMensal(ano, mes, reservas.size(), ocupadas, faturamento, servicosCount);
    }

    public static record RelatorioMensal(int ano, int mes, long totalReservas, long totalOcupadas, BigDecimal faturamento, long totalServicos) {}
}