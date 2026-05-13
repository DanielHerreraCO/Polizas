package com.grupo_bolivar.polizas.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "ESTADO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Estado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idestado;

    @Column(nullable = false, length = 50)
    private String codigo;

    @Column(nullable = false, length = 50)
    private String nombre;

    @Column(length = 50)
    private String descripcion;

    @Column(nullable = false, length = 50)
    private String origen;

    @Column(nullable = false)
    private Boolean indicador;
}