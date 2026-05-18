package br.com.estacionamento.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ClienteDTO(
        Integer id,
        @NotBlank @Size(max = 100) String nome,
        @Size(max = 14) String cpf,
        String telefonePrincipal
) {}
