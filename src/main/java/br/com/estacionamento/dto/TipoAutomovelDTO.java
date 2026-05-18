package br.com.estacionamento.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record TipoAutomovelDTO(
        Integer id,
        @NotBlank @Size(max = 50) String nome,
        @NotNull @PositiveOrZero BigDecimal valorHora
) {}
