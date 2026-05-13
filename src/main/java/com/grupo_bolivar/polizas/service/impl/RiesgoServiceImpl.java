package com.grupo_bolivar.polizas.service.impl;

import com.grupo_bolivar.polizas.entity.Estado;
import com.grupo_bolivar.polizas.entity.PolizaRiesgo;
import com.grupo_bolivar.polizas.entity.Riesgo;
import com.grupo_bolivar.polizas.repository.PolizaRepository;
import com.grupo_bolivar.polizas.repository.PolizaRiesgoRepository;
import com.grupo_bolivar.polizas.repository.RiesgoRepository;
import com.grupo_bolivar.polizas.service.RiesgoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RiesgoServiceImpl implements RiesgoService {

    private final RiesgoRepository riesgoRepository;
    private final PolizaRiesgoRepository polizaRiesgoRepository;

    @Override
    public List<Riesgo> obtenerRiesgos() {
        return riesgoRepository.findAll();
    }

    @Override
    public Riesgo guardar(Riesgo riesgo) {
        return riesgoRepository.save(riesgo);
    }

    @Override
    public Riesgo cancelarRiesgo(Long polizaRiesgoId) {

        PolizaRiesgo pr = polizaRiesgoRepository.findById(polizaRiesgoId)
                .orElseThrow();

        pr.setEstado(
                Estado.builder()
                        .idestado(0L) // ejemplo: 0 = CANCELADO
                        .build()
        );

        polizaRiesgoRepository.save(pr);

        return pr.getRiesgo();
    }
}
