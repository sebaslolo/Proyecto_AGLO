package com.Proyecto_Grupo_1.repository;

import com.Proyecto_Grupo_1.domain.ActividadDetalle;
import com.Proyecto_Grupo_1.domain.ActividadDetalleId;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActividadDetalleRepository extends JpaRepository<ActividadDetalle, ActividadDetalleId> {

    List<ActividadDetalle> findByReservacionIdReservacion(Integer idReservacion);

    List<ActividadDetalle> findByActividadIdActividad(Integer idActividad);
}
