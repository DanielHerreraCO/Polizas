package com.grupo_bolivar.polizas.controller;

import com.grupo_bolivar.polizas.entity.EventoRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/core-mock")
@Slf4j
public class CoreMockController {

    private static final String API_KEY = "123456";


    @PostMapping("/evento")
    public String enviarEvento(
            @RequestHeader(value = "x-api-key", required = true) String apiKey,
            @RequestBody EventoRequest request
    ) {

        // 🔐 SEGURIDAD MÍNIMA
        if (!API_KEY.equals(apiKey)) {
            throw new RuntimeException("API KEY inválida");
        }

        // 📌 MOCK CORE
        log.info("ENVÍO A CORE MOCK -> evento: {}, polizaId: {}",
                request.getEvento(),
                request.getPolizaId()
        );

        return "EVENTO RECIBIDO EN CORE MOCK";
    }
}
