package com.grupo_bolivar.polizas.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Table(name = "PolizaRiesgo")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PolizaRiesgo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPolizaRiesgo;

    @ManyToOne
    @JoinColumn(name = "idPoliza")
    @JsonBackReference
    private Poliza poliza;

    @ManyToOne
    @JoinColumn(name = "idRiesgo")
    private Riesgo riesgo;

    @ManyToOne
    @JoinColumn(name = "idestado")
    private Estado estado;

    @Temporal(TemporalType.DATE)
    private Date fechaAsignacionRiesgo;
}