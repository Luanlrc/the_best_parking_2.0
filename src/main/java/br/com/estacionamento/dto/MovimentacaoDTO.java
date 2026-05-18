package br.com.estacionamento.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record MovimentacaoDTO(
        Integer id,
        LocalDateTime entrada,
        LocalDateTime saida,
        BigDecimal valorCobrado,
        Integer idAutomovel,
        String placa,
        Integer idVaga,
        String numeroVaga
) {}
