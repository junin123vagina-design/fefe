package com.hotel.management.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "quartos", uniqueConstraints = @UniqueConstraint(columnNames = {"numero"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Quarto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String numero;

    private String tipo;

    @Enumerated(EnumType.STRING)
    private Status status = Status.DISPONIVEL;

    private BigDecimal precoPorNoite;

    public enum Status {
        DISPONIVEL, OCUPADO, MANUTENCAO
    }
}