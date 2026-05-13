package com.grupo_bolivar.polizas.entity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;


@Entity
@Table(name = "Riesgo")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Riesgo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idRiesgo")
    private Integer idRiesgo;
    @ManyToOne
    @JoinColumn(name = "idestado")
    private Estado estado;
    @Column(name = "descripcion")
    private String descripcion;
    @Column(name = "valorAsegurado")
    private Double valorAsegurado;
    @Column(name = "direccionInmueble")
    private String direccionInmueble;
    @OneToMany(mappedBy = "riesgo", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<PolizaRiesgo> polizaRiesgos;
}
