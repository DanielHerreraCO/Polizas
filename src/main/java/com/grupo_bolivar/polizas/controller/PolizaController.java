package com.grupo_bolivar.polizas.controller;

import com.grupo_bolivar.polizas.entity.Poliza;
import com.grupo_bolivar.polizas.entity.Riesgo;
import com.grupo_bolivar.polizas.service.PolizaService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/polizas")
@RequiredArgsConstructor
public class PolizaController {

    private final PolizaService polizaService;

    @GetMapping
    public List<Poliza> listarPolizas(
            @RequestParam(required = false) String tipo,
            @RequestParam(required = false) String estado
    ) {
        return polizaService.listarPolizas(tipo, estado);
    }

    @GetMapping("/{id}/riesgos")
    public List<Riesgo> obtenerRiesgos(@PathVariable Long id) {
        return polizaService.obtenerRiesgos(id);
    }

    @PostMapping("/{id}/renovar")
    public Poliza renovarPoliza(@PathVariable Long id) {
        return polizaService.renovarPoliza(id);
    }

    @PostMapping("/{id}/cancelar")
    public Poliza cancelarPoliza(@PathVariable Long id) {
        return polizaService.cancelarPoliza(id);
    }

    @PostMapping("/{id}/riesgos")
    public Riesgo agregarRiesgo(
            @PathVariable Long id,
            @RequestBody Riesgo riesgo
    ) {
        
        return polizaService.agregarRiesgo(id, riesgo);
    }
}
