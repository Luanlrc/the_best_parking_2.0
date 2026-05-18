package br.com.estacionamento.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record VagaDTO(
        Integer id,
        @NotBlank @Size(max = 10) String numero,
        @Size(max = 20) String status
) {}
