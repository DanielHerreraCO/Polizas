package com.grupo_bolivar.polizas.repository;

import com.grupo_bolivar.polizas.entity.Poliza;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PolizaRepository extends JpaRepository<Poliza, Long>{
}
