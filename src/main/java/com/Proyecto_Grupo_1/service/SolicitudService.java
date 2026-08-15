package com.Proyecto_Grupo_1.service;

import com.Proyecto_Grupo_1.domain.Solicitud;
import com.Proyecto_Grupo_1.repository.SolicitudRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SolicitudService {

    private final SolicitudRepository solicitudRepository;

    public SolicitudService(
            SolicitudRepository solicitudRepository) {

        this.solicitudRepository =
                solicitudRepository;
    }

    @Transactional(readOnly = true)
    public boolean yaEnvioRetroalimentacion(
            Integer idUsuario,
            Integer idActividad) {

        return solicitudRepository
                .existsByUsuarioIdUsuarioAndActividadIdActividadAndTipoSolicitudNombreIgnoreCase(
                        idUsuario,
                        idActividad,
                        "Retroalimentacion");
    }

    @Transactional
    public Solicitud save(
            Solicitud solicitud) {

        return solicitudRepository
                .save(solicitud);
    }

    @Transactional(readOnly = true)
    public List<Solicitud> getSolicitudesPorUsuario(
            Integer idUsuario) {

        return solicitudRepository
                .findByUsuarioIdUsuarioOrderByFechaSolicitudDesc(
                        idUsuario);
    }

    @Transactional(readOnly = true)
    public List<Solicitud> getRetroalimentaciones() {

        return solicitudRepository
                .findByTipoSolicitudNombreIgnoreCaseOrderByFechaSolicitudDesc(
                        "Retroalimentacion");
    }

    @Transactional(readOnly = true)
    public Solicitud obtenerSolicitud(
            Integer idSolicitud) {

        return solicitudRepository
                .findById(idSolicitud)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Solicitud no encontrada: "
                                + idSolicitud));
    }
}