package br.com.estacionamento.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CheckinRequest(
        @NotBlank String placa,
        @NotNull Integer idVaga
) {}
