package com.Proyecto_Grupo_1.repository;

import com.Proyecto_Grupo_1.domain.ActividadDetalle;
import com.Proyecto_Grupo_1.domain.ActividadDetalleId;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ActividadDetalleRepository extends JpaRepository<ActividadDetalle, ActividadDetalleId> {

    List<ActividadDetalle> findByReservacionIdReservacion(Integer idReservacion);

    List<ActividadDetalle> findByActividadIdActividad(Integer idActividad);

    @Query("select coalesce(sum(detalle.cantidadPersonas), 0) from ActividadDetalle detalle where detalle.actividad.idActividad = :idActividad")
    Long sumarPersonasPorActividad(@Param("idActividad") Integer idActividad);
}
