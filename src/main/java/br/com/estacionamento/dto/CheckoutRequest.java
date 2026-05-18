package br.com.estacionamento.dto;

import jakarta.validation.constraints.NotBlank;

public record CheckoutRequest(
        @NotBlank String placa
) {}
