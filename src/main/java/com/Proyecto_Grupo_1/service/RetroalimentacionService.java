package com.Proyecto_Grupo_1.service;

import com.Proyecto_Grupo_1.domain.Estado;
import com.Proyecto_Grupo_1.domain.InscripcionVoluntariado;
import com.Proyecto_Grupo_1.domain.Pregunta;
import com.Proyecto_Grupo_1.domain.Respuesta;
import com.Proyecto_Grupo_1.domain.Solicitud;
import com.Proyecto_Grupo_1.domain.Usuario;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RetroalimentacionService {

    private final SolicitudService solicitudService;
    private final TipoSolicitudService tipoSolicitudService;
    private final PreguntaService preguntaService;
    private final RespuestaService respuestaService;

    public RetroalimentacionService(
            SolicitudService solicitudService,
            TipoSolicitudService tipoSolicitudService,
            PreguntaService preguntaService,
            RespuestaService respuestaService) {

        this.solicitudService =
                solicitudService;

        this.tipoSolicitudService =
                tipoSolicitudService;

        this.preguntaService =
                preguntaService;

        this.respuestaService =
                respuestaService;
    }

    @Transactional(readOnly = true)
    public List<Solicitud> getRetroalimentaciones() {

        return solicitudService
                .getRetroalimentaciones();
    }

    @Transactional(readOnly = true)
    public boolean yaEnvioRetroalimentacion(
            Integer idUsuario,
            Integer idActividad) {

        return solicitudService
                .yaEnvioRetroalimentacion(
                        idUsuario,
                        idActividad);
    }

    @Transactional
    public void guardarRetroalimentacion(
            Usuario usuario,
            InscripcionVoluntariado inscripcion,
            Integer puntuacion,
            String comentarios,
            Estado estado) {

        if (usuario == null
                || inscripcion == null
                || inscripcion.getActividad() == null) {

            throw new IllegalArgumentException(
                    "La información del voluntariado es inválida.");
        }

        Integer idUsuario =
                usuario.getIdUsuario();

        Integer idActividad =
                inscripcion.getActividad()
                        .getIdActividad();

        if (yaEnvioRetroalimentacion(
                idUsuario,
                idActividad)) {

            throw new IllegalStateException(
                    "Ya enviaste retroalimentación para este voluntariado.");
        }

        if (puntuacion == null
                || puntuacion < 1
                || puntuacion > 5) {

            throw new IllegalArgumentException(
                    "La puntuación debe estar entre 1 y 5.");
        }

        var tipoSolicitud =
                tipoSolicitudService
                        .obtenerPorNombre(
                                "Retroalimentacion");

        Solicitud solicitud =
                new Solicitud();

        solicitud.setUsuario(
                usuario);

        solicitud.setActividad(
                inscripcion.getActividad());

        solicitud.setTipoSolicitud(
                tipoSolicitud);

        solicitud.setEstado(
                estado);

        solicitud.setFechaEnvio(
                LocalDateTime.now());

        solicitud =
                solicitudService.save(
                        solicitud);

        List<Pregunta> preguntas =
                preguntaService.getPreguntas();

        for (Pregunta pregunta : preguntas) {

            if (pregunta.getTipoRespuesta() == null
                    || pregunta.getTipoRespuesta()
                            .getNombre() == null) {

                continue;
            }

            String tipoRespuesta =
                    pregunta.getTipoRespuesta()
                            .getNombre();

            if (tipoRespuesta.equalsIgnoreCase(
                    "Calificacion de 1 a 5")) {

                guardarRespuesta(
                        solicitud,
                        pregunta,
                        String.valueOf(puntuacion),
                        estado);
            }

            if (tipoRespuesta.equalsIgnoreCase(
                    "Comentario libre")) {

                String comentario =
                        comentarios == null
                        ? ""
                        : comentarios;

                guardarRespuesta(
                        solicitud,
                        pregunta,
                        comentario,
                        estado);
            }
        }
    }

    private void guardarRespuesta(
            Solicitud solicitud,
            Pregunta pregunta,
            String valor,
            Estado estado) {

        Respuesta respuesta =
                new Respuesta();

        respuesta.setSolicitud(
                solicitud);

        respuesta.setPregunta(
                pregunta);

        respuesta.setRespuesta(
                valor);

        respuesta.setEstado(
                estado);

        respuestaService.save(
                respuesta);
    }
}