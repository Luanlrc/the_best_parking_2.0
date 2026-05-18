package br.com.estacionamento.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AutomovelDTO(
        Integer id,
        @NotBlank @Size(max = 8) String placa,
        @Size(max = 80) String modelo,
        @Size(max = 30) String cor,
        @NotNull Boolean mensalista,
        Integer idCliente,
        @NotNull Integer idTipo
) {}
