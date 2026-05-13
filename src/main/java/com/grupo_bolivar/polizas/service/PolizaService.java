package com.grupo_bolivar.polizas.service;

import com.grupo_bolivar.polizas.entity.Poliza;
import com.grupo_bolivar.polizas.entity.Riesgo;

import java.util.List;

public interface PolizaService {
    List<Poliza> obtenerTodas();
    Poliza guardar(Poliza poliza);
    List<Poliza> listarPolizas(String tipo, String estado);
    List<Riesgo> obtenerRiesgos(Long polizaId);
    Poliza renovarPoliza(Long polizaId);
    Poliza cancelarPoliza(Long polizaId);
    Riesgo agregarRiesgo(Long polizaId, Riesgo riesgo);

}
