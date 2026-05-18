package br.com.estacionamento.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "vaga")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vaga {

    public static final String STATUS_LIVRE = "LIVRE";
    public static final String STATUS_OCUPADA = "OCUPADA";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_vaga")
    private Integer id;

    @Column(name = "numero", nullable = false, length = 10)
    private String numero;

    @Column(name = "status", nullable = false, length = 20)
    private String status;
}
