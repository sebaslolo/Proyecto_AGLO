package com.Proyecto_Grupo_1.service;

import com.Proyecto_Grupo_1.domain.Respuesta;
import com.Proyecto_Grupo_1.repository.RespuestaRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RespuestaService {

    private final RespuestaRepository respuestaRepository;

    public RespuestaService(
            RespuestaRepository respuestaRepository) {

        this.respuestaRepository =
                respuestaRepository;
    }

    @Transactional
    public Respuesta save(
            Respuesta respuesta) {

        return respuestaRepository
                .save(respuesta);
    }

    @Transactional(readOnly = true)
    public List<Respuesta> getRespuestasPorSolicitud(
            Integer idSolicitud) {

        return respuestaRepository
                .findBySolicitudIdSolicitud(
                        idSolicitud);
    }

    @Transactional(readOnly = true)
    public boolean existeRespuesta(
            Integer idSolicitud,
            Integer idPregunta) {

        return respuestaRepository
                .existsBySolicitudIdSolicitudAndPreguntaIdPregunta(
                        idSolicitud,
                        idPregunta);
    }
}