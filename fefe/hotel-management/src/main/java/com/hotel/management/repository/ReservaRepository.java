package com.hotel.management.repository;

import com.hotel.management.model.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    // busca reservas que se sobrepõem para o mesmo quarto (não canceladas)
    @Query("select r from Reserva r " +
            "where r.quarto.id = :quartoId " +
            "and r.status <> com.hotel.management.model.Reserva.StatusReserva.CANCELADO " +
            "and r.dataInicio < :dataFim and r.dataFim > :dataInicio")
    List<Reserva> findReservasSobrepostas(@Param("quartoId") Long quartoId,
                                          @Param("dataInicio") LocalDate dataInicio,
                                          @Param("dataFim") LocalDate dataFim);

    // buscar reservas dentro de um mês (para relatórios)
    @Query("select r from Reserva r where (r.dataInicio between :inicio and :fim) or (r.dataFim between :inicio and :fim)")
    List<Reserva> findReservasNoPeriodo(@Param("inicio") LocalDate inicio, @Param("fim") LocalDate fim);
}