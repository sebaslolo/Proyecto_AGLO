package com.Proyecto_Grupo_1.repository;

import com.Proyecto_Grupo_1.domain.Estado;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EstadoRepository extends JpaRepository<Estado, Integer> {

    Optional<Estado> findByNombreEstadoIgnoreCase(String nombreEstado);
}
