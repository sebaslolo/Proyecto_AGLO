package com.Proyecto_Grupo_1.repository;

import com.Proyecto_Grupo_1.domain.Actividad;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActividadRepository extends JpaRepository<Actividad, Integer> {

    List<Actividad> findByFechaHoraInicioAfterOrderByFechaHoraInicioAsc(LocalDateTime fechaHoraInicio);

    List<Actividad> findByNombreActividadContainingIgnoreCase(String nombreActividad);
}
