package com.grupo_bolivar.polizas.service.impl;

import com.grupo_bolivar.polizas.entity.*;
import com.grupo_bolivar.polizas.repository.EstadoRepository;
import com.grupo_bolivar.polizas.repository.PolizaRepository;
import com.grupo_bolivar.polizas.repository.PolizaRiesgoRepository;
import com.grupo_bolivar.polizas.repository.RiesgoRepository;
import com.grupo_bolivar.polizas.service.PolizaService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PolizaServiceImpl implements PolizaService {

    private final PolizaRepository polizaRepository;
    private final RiesgoRepository riesgoRepository;
    private final PolizaRiesgoRepository polizaRiesgoRepository;
    private final EstadoRepository estadoRepository;

    @Override
    public List<Poliza> obtenerTodas() {
        return polizaRepository.findAll();
    }

    @Override
    public Poliza guardar(Poliza poliza) {
        return polizaRepository.save(poliza);
    }

    @Override
    public List<Poliza> listarPolizas(String tipo, String estado) {
        if (tipo != null && estado != null) {
            return polizaRepository.findAll().stream()
                    .filter(p -> tipo.equals(p.getTipoPoliza())
                            && estado.equals(String.valueOf(p.getIdestado())))
                    .toList();
        }

        if (tipo != null) {
            return polizaRepository.findAll().stream()
                    .filter(p -> tipo.equals(p.getTipoPoliza()))
                    .toList();
        }

        if (estado != null) {
            return polizaRepository.findAll().stream()
                    .filter(p -> estado.equals(String.valueOf(p.getIdestado())))
                    .toList();
        }

        return polizaRepository.findAll();
    }

    @Transactional
    @Override
    public List<Riesgo> obtenerRiesgos(Long polizaId) {
        return polizaRepository.findById((long) polizaId.intValue())
                .map(p -> p.getPolizaRiesgos()
                        .stream()
                        .map(PolizaRiesgo::getRiesgo)
                        .toList()
                )
                .orElse(List.of());
    }

    @Override
    public Poliza renovarPoliza(Long polizaId) {

        Poliza p = polizaRepository.findById(polizaId)
                .orElseThrow(() -> new RuntimeException("Poliza no existe"));

        if (p.getIdestado() == 2) {
            return ApiResponse.<Poliza>builder()
                    .success(false)
                    .message("No se puede renovar una póliza cancelada")
                    .data(p)
                    .build()
                    .getData();
        }

        Estado estadoRenovado = estadoRepository.findById(3L)
                .orElseThrow();

        p.setIdestado(3);

        List<PolizaRiesgo> riesgos =
                polizaRiesgoRepository.findByPoliza_IdPoliza(polizaId);

        riesgos.forEach(r -> r.setEstado(estadoRenovado));

        polizaRiesgoRepository.saveAll(riesgos);
        Poliza updated = polizaRepository.save(p);

        return ApiResponse.<Poliza>builder()
                .success(true)
                .message("Póliza renovada correctamente")
                .data(updated)
                .build()
                .getData();
    }

    @Override
    public Poliza cancelarPoliza(Long polizaId) {

        Poliza p = polizaRepository.findById(polizaId)
                .orElseThrow(() -> new RuntimeException("Poliza no existe"));

        // estado cancelado
        Estado estadoCancelado = estadoRepository.findById(2L)
                .orElseThrow(() -> new RuntimeException("Estado no existe"));

        // actualizar estado póliza
        p.setIdestado(2);

        // buscar relaciones
        List<PolizaRiesgo> riesgos =
                polizaRiesgoRepository.findByPoliza_IdPoliza(polizaId);

        // actualizar estado de cada relación
        riesgos.forEach(r -> r.setEstado(estadoCancelado));

        // guardar cambios
        polizaRiesgoRepository.saveAll(riesgos);

        Poliza cancelada = polizaRepository.save(p);

        return ApiResponse.<Poliza>builder()
                .success(true)
                .message("Póliza cancelada correctamente")
                .data(cancelada)
                .build()
                .getData();
    }

    @Override
    public Riesgo agregarRiesgo(Long polizaId, Riesgo riesgoRequest) {

        Poliza poliza = polizaRepository.findById(polizaId)
                .orElseThrow(() -> new RuntimeException("Poliza no existe"));

        // 🔥 IMPORTANTE: traer riesgo REAL de BD
        Riesgo riesgo = riesgoRepository.findById(riesgoRequest.getIdRiesgo().longValue())
                .orElseThrow(() -> new RuntimeException("Riesgo no existe"));

        // REGLA 1
        if ("INDIVIDUAL".equalsIgnoreCase(poliza.getTipoPoliza())
                && !poliza.getPolizaRiesgos().isEmpty()) {
            throw new RuntimeException("Una póliza individual solo puede tener 1 riesgo");
        }

        // REGLA 2
        if (!"INDIVIDUAL".equalsIgnoreCase(poliza.getTipoPoliza())
                && !"COLECTIVA".equalsIgnoreCase(poliza.getTipoPoliza())) {
            throw new RuntimeException("Tipo de póliza no permitido");
        }

        PolizaRiesgo pr = PolizaRiesgo.builder()
                .poliza(poliza)
                .riesgo(riesgo)
                .estado(riesgo.getEstado())
                .fechaAsignacionRiesgo(new java.util.Date())
                .build();

        poliza.getPolizaRiesgos().add(pr);

        polizaRepository.save(poliza);

        return riesgo;
    }
}

