package com.Proyecto_Grupo_1.service;

import com.Proyecto_Grupo_1.domain.Actividad;
import com.Proyecto_Grupo_1.domain.Estado;
import com.Proyecto_Grupo_1.domain.InscripcionVoluntariado;
import com.Proyecto_Grupo_1.domain.Usuario;
import com.Proyecto_Grupo_1.domain.Voluntariado;
import com.Proyecto_Grupo_1.repository.InscripcionVoluntariadoRepository;
import com.Proyecto_Grupo_1.repository.VoluntariadoRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class VoluntariadoService {

    private final VoluntariadoRepository voluntariadoRepository;
    private final InscripcionVoluntariadoRepository inscripcionVoluntariadoRepository;
    private final ActividadService actividadService;
    private final UsuarioService usuarioService;
    private final EstadoService estadoService;

    public VoluntariadoService(
            VoluntariadoRepository voluntariadoRepository,
            InscripcionVoluntariadoRepository inscripcionVoluntariadoRepository,
            ActividadService actividadService,
            UsuarioService usuarioService,
            EstadoService estadoService) {

        this.voluntariadoRepository = voluntariadoRepository;
        this.inscripcionVoluntariadoRepository = inscripcionVoluntariadoRepository;
        this.actividadService = actividadService;
        this.usuarioService = usuarioService;
        this.estadoService = estadoService;
    }

    @Transactional(readOnly = true)
    public long obtenerCantidadInscritos(Integer idActividad) {

        return inscripcionVoluntariadoRepository
                .countByActividadIdActividad(idActividad);
    }

    @Transactional(readOnly = true)
    public long obtenerCuposDisponibles(Integer idActividad) {

        Actividad actividad =
                actividadService.obtenerActividad(idActividad);

        long cupoMaximo =
                actividad.getCupoMaximo() == null
                ? 0
                : actividad.getCupoMaximo();

        long inscritos =
                obtenerCantidadInscritos(idActividad);

        return Math.max(0, cupoMaximo - inscritos);
    }

    @Transactional(readOnly = true)
    public boolean usuarioYaInscrito(
            Integer idUsuario,
            Integer idActividad) {

        return inscripcionVoluntariadoRepository
                .existsByVoluntariadoUsuarioIdUsuarioAndActividadIdActividad(
                        idUsuario,
                        idActividad);
    }

    @Transactional
    public InscripcionVoluntariado inscribir(
            Integer idUsuario,
            Integer idActividad) {

        Actividad actividad =
                actividadService.obtenerActividad(idActividad);

        Usuario usuario =
                usuarioService.obtenerUsuario(idUsuario);

        if (actividad.getFechaHoraInicio()
                .isBefore(LocalDateTime.now())) {

            throw new IllegalStateException(
                    "Este voluntariado ya inició o finalizó.");
        }

        if (usuarioYaInscrito(
                idUsuario,
                idActividad)) {

            throw new IllegalStateException(
                    "Ya te encuentras inscrito en este voluntariado.");
        }

        if (obtenerCuposDisponibles(idActividad) <= 0) {

            throw new IllegalStateException(
                    "No hay cupos disponibles para este voluntariado.");
        }

        Voluntariado voluntariado =
                obtenerOCrearVoluntario(usuario);

        Estado estadoInscripcion =
                estadoService.obtenerEstadoPorNombre(
                        "Confirmada");

        InscripcionVoluntariado inscripcion =
                new InscripcionVoluntariado();

        inscripcion.setVoluntariado(voluntariado);
        inscripcion.setActividad(actividad);
        inscripcion.setFechaInscripcion(
                LocalDateTime.now());
        inscripcion.setEstado(
                estadoInscripcion);

        return inscripcionVoluntariadoRepository
                .save(inscripcion);
    }

    private Voluntariado obtenerOCrearVoluntario(
            Usuario usuario) {

        Optional<Voluntariado> voluntariadoExistente =
                voluntariadoRepository
                        .findByUsuarioIdUsuario(
                                usuario.getIdUsuario());

        if (voluntariadoExistente.isPresent()) {

            return voluntariadoExistente.get();
        }

        Estado estadoActivo =
                estadoService.obtenerEstadoPorNombre(
                        "Activo");

        Voluntariado voluntariado =
                new Voluntariado();

        voluntariado.setUsuario(usuario);
        voluntariado.setFechaIngreso(
                LocalDate.now());
        voluntariado.setDisponibilidad(null);
        voluntariado.setHorasAcumuladas(
                BigDecimal.ZERO);
        voluntariado.setEstado(
                estadoActivo);

        return voluntariadoRepository
                .save(voluntariado);
    }

    @Transactional(readOnly = true)
    public List<InscripcionVoluntariado>
            listarPorUsuario(Integer idUsuario) {

        return inscripcionVoluntariadoRepository
                .findByVoluntariadoUsuarioIdUsuarioOrderByFechaInscripcionDesc(
                        idUsuario);
    }

    @Transactional(readOnly = true)
    public List<InscripcionVoluntariado>
            getVoluntariadosPorUsuario(
                    Integer idUsuario) {

        return listarPorUsuario(idUsuario);
    }

    @Transactional(readOnly = true)
    public List<InscripcionVoluntariado>
            getVoluntariados(boolean sinFiltro) {

        return inscripcionVoluntariadoRepository
                .findAll();
    }

    @Transactional(readOnly = true)
    public Optional<InscripcionVoluntariado>
            getVoluntariado(
                    Integer idInscripcion) {

        return inscripcionVoluntariadoRepository
                .findById(idInscripcion);
    }

    @Transactional(readOnly = true)
    public InscripcionVoluntariado
            obtenerVoluntariado(
                    Integer idInscripcion) {

        return inscripcionVoluntariadoRepository
                .findById(idInscripcion)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Inscripción de voluntariado no encontrada: "
                                + idInscripcion));
    }

    @Transactional(readOnly = true)
    public boolean puedeEnviarRetroalimentacion(
            Integer idInscripcion) {

        InscripcionVoluntariado voluntariado =
                obtenerVoluntariado(idInscripcion);

        if (voluntariado.getActividad() == null
                || voluntariado.getActividad()
                        .getFechaHoraInicio() == null) {

            return false;
        }

        return !LocalDateTime.now().isBefore(
                voluntariado.getActividad()
                        .getFechaHoraInicio());
    }
}