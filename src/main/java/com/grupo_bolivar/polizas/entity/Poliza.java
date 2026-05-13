package com.grupo_bolivar.polizas.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.List;

@Entity
@Table(name = "Poliza")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Poliza {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idPoliza")
    @JsonIgnore
    private Integer idPoliza;
    @Column(name = "idestado")
    private Integer idestado;
    @Column(name = "tipoPoliza")
    private String tipoPoliza;
    @Column(name = "fechaInicio")
    private Date fechaInicio;
    @Column(name = "fechaFin")
    private Date fechaFin;
    @Column(name = "canon_mensual")
    private Double canon_mensual;
    @Column(name = "porcentaje_ipc")
    private Double porcentaje_ipc;
    @Column(name = "valor_prima")
    private Double valor_prima;
    @Column(name = "arrendatario")
    private String arrendatario;
    @Column(name = "arrendador")
    private String arrendador;
    @OneToMany(mappedBy = "poliza", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<PolizaRiesgo> polizaRiesgos;
}
