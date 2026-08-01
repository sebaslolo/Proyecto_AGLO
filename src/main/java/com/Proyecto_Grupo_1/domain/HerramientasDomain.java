package com.Proyecto_Grupo_1.repository;

import com.Proyecto_Grupo_1.domain.Herramienta;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HerramientaRepository extends JpaRepository<Herramienta, Integer>{

    Optional<Herramienta> findByNombreHerramientaIgnoreCase(String nombreHerramienta);

}