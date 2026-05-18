package br.com.estacionamento.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "movimentacao")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Movimentacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_movimentacao")
    private Integer id;

    @Column(name = "entrada", nullable = false)
    private LocalDateTime entrada;

    @Column(name = "saida")
    private LocalDateTime saida;

    @Column(name = "valor_cobrado", precision = 10, scale = 2)
    private BigDecimal valorCobrado;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_automovel", nullable = false)
    private Automovel automovel;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_vaga", nullable = false)
    private Vaga vaga;
}
