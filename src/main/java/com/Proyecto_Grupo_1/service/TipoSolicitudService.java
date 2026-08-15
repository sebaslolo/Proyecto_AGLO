package com.Proyecto_Grupo_1.service;

import com.Proyecto_Grupo_1.domain.TipoSolicitud;
import com.Proyecto_Grupo_1.repository.TipoSolicitudRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TipoSolicitudService {

    private final TipoSolicitudRepository tipoSolicitudRepository;

    public TipoSolicitudService(
            TipoSolicitudRepository tipoSolicitudRepository) {

        this.tipoSolicitudRepository =
                tipoSolicitudRepository;
    }

    @Transactional(readOnly = true)
    public TipoSolicitud obtenerPorNombre(
            String nombre) {

        return tipoSolicitudRepository
                .findByNombreIgnoreCase(nombre)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Tipo de solicitud no encontrado: "
                                + nombre));
    }
}