package com.Proyecto_Grupo_1.repository;

import com.Proyecto_Grupo_1.domain.Solicitud;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SolicitudRepository
        extends JpaRepository<Solicitud, Integer> {

    boolean existsByUsuarioIdUsuarioAndActividadIdActividadAndTipoSolicitudNombreIgnoreCase(
            Integer idUsuario,
            Integer idActividad,
            String nombreTipoSolicitud);

    List<Solicitud> findByUsuarioIdUsuarioOrderByFechaSolicitudDesc(
            Integer idUsuario);

    List<Solicitud> findByTipoSolicitudNombreIgnoreCaseOrderByFechaSolicitudDesc(
            String nombreTipoSolicitud);
}