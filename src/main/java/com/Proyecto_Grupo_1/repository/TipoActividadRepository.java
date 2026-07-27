package com.Proyecto_Grupo_1.repository;

import com.Proyecto_Grupo_1.domain.TipoActividad;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TipoActividadRepository extends JpaRepository<TipoActividad, Integer> {

    Optional<TipoActividad> findByNombreTipoActividadIgnoreCase(String nombreTipoActividad);
}
