package com.grupo_bolivar.polizas.repository;

import com.grupo_bolivar.polizas.entity.PolizaRiesgo;
import com.grupo_bolivar.polizas.entity.Riesgo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PolizaRiesgoRepository extends JpaRepository<PolizaRiesgo, Long> {
    List<PolizaRiesgo> findByPoliza_IdPoliza(Long polizaId);
}
