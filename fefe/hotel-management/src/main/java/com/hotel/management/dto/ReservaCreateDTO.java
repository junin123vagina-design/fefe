package com.hotel.management.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservaCreateDTO {
    @NotNull
    private Long clienteId;
    @NotNull
    private Long quartoId;
    @NotNull
    private LocalDate dataInicio;
    @NotNull
    private LocalDate dataFim;
}