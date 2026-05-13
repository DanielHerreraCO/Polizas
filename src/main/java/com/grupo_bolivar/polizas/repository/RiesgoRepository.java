package com.grupo_bolivar.polizas.repository;

import com.grupo_bolivar.polizas.entity.Riesgo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RiesgoRepository extends JpaRepository<Riesgo, Long> {
}
