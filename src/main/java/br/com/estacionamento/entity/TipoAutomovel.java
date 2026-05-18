package br.com.estacionamento.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "tipo_automovel")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TipoAutomovel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tipo")
    private Integer id;

    @Column(name = "nome", nullable = false, length = 50)
    private String nome;

    @Column(name = "valor_hora", nullable = false, precision = 10, scale = 2)
    private BigDecimal valorHora;
}
