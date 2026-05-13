package com.grupo_bolivar.polizas.controller;

import com.grupo_bolivar.polizas.entity.Riesgo;
import com.grupo_bolivar.polizas.service.RiesgoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/riesgos")
@RequiredArgsConstructor
public class RiesgoController {
    private final RiesgoService riesgoService;

    @PostMapping("/{id}/cancelar")
    public Riesgo cancelarRiesgo(@PathVariable Long id) {
        return riesgoService.cancelarRiesgo(id);
    }
}
