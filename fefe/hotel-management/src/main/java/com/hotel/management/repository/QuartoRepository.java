package com.hotel.management.repository;

import com.hotel.management.model.Quarto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface QuartoRepository extends JpaRepository<Quarto, Long> {
    Optional<Quarto> findByNumero(String numero);
}