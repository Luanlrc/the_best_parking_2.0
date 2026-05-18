package br.com.estacionamento.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "automovel")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Automovel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_automovel")
    private Integer id;

    @Column(name = "placa", nullable = false, length = 8, unique = true)
    private String placa;

    @Column(name = "modelo", length = 80)
    private String modelo;

    @Column(name = "cor", length = 30)
    private String cor;

    @Column(name = "mensalista", nullable = false)
    private Boolean mensalista;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cliente")
    private Cliente cliente;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_tipo", nullable = false)
    private TipoAutomovel tipo;
}
