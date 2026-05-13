package com.grupo_bolivar.polizas.service;

import com.grupo_bolivar.polizas.entity.Poliza;
import com.grupo_bolivar.polizas.entity.Riesgo;

import java.util.List;

public interface RiesgoService {
    List<Riesgo> obtenerRiesgos();
    Riesgo guardar(Riesgo riesgo);
    Riesgo cancelarRiesgo(Long id);
}
