package com.Proyecto_Grupo_1.service;

import com.Proyecto_Grupo_1.domain.Actividad;
import com.Proyecto_Grupo_1.domain.Usuario;
import com.Proyecto_Grupo_1.domain.Voluntariado;
import com.Proyecto_Grupo_1.repository.VoluntariadoRepository;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
public class VoluntariadoService {

    private final VoluntariadoRepository voluntariadoRepository;
    private final ActividadService actividadService;
    private final UsuarioService usuarioService;

    public VoluntariadoService(
            VoluntariadoRepository voluntariadoRepository,
            ActividadService actividadService,
            UsuarioService usuarioService) {

        this.voluntariadoRepository = voluntariadoRepository;
        this.actividadService = actividadService;
        this.usuarioService = usuarioService;
    }

    // Métodos de inscripción a voluntariados

    @Transactional(readOnly = true)
    public long obtenerCantidadInscritos(Integer idActividad) {
        return voluntariadoRepository.countByActividadIdActividad(idActividad);
    }

    @Transactional(readOnly = true)
    public long obtenerCuposDisponibles(Integer idActividad) {

        Actividad actividad = actividadService.obtenerActividad(idActividad);

        long cupoMaximo = actividad.getCupoMaximo() == null
                ? 0
                : actividad.getCupoMaximo();

        long inscritos = obtenerCantidadInscritos(idActividad);

        return Math.max(0, cupoMaximo - inscritos);
    }

    @Transactional(readOnly = true)
    public boolean usuarioYaInscrito(
            Integer idUsuario,
            Integer idActividad) {

        return voluntariadoRepository
                .existsByUsuarioIdUsuarioAndActividadIdActividad(
                        idUsuario,
                        idActividad);
    }

    @Transactional
    public Voluntariado inscribir(
            Integer idUsuario,
            Integer idActividad,
            String herramientasUtilizadas) {

        Actividad actividad = actividadService.obtenerActividad(idActividad);
        Usuario usuario = usuarioService.obtenerUsuario(idUsuario);

        if (actividad.getFechaHoraInicio().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException(
                    "Este voluntariado ya inició o finalizó.");
        }

        if (usuarioYaInscrito(idUsuario, idActividad)) {
            throw new IllegalStateException(
                    "Ya te encuentras inscrito en este voluntariado.");
        }

        if (obtenerCuposDisponibles(idActividad) <= 0) {
            throw new IllegalStateException(
                    "No hay cupos disponibles para este voluntariado.");
        }

        Voluntariado inscripcion = new Voluntariado();

        inscripcion.setUsuario(usuario);
        inscripcion.setActividad(actividad);
        inscripcion.setHerramientasUtilizadas(herramientasUtilizadas);
        inscripcion.setFechaInscripcion(LocalDateTime.now());

        return voluntariadoRepository.save(inscripcion);
    }

    @Transactional(readOnly = true)
    public List<Voluntariado> listarPorUsuario(Integer idUsuario) {
        return voluntariadoRepository
                .findByUsuarioIdUsuarioOrderByFechaInscripcionDesc(idUsuario);
    }

    // Métodos utilizados por HU-5 Retroalimentación

    @Transactional(readOnly = true)
    public List<Voluntariado> getVoluntariados(boolean sinFiltro) {
        return voluntariadoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Voluntariado> getVoluntariado(Integer idVoluntariado) {
        return voluntariadoRepository.findById(idVoluntariado);
    }

    @Transactional(readOnly = true)
    public List<Voluntariado> getVoluntariadosPorUsuario(Integer idUsuario) {
        return voluntariadoRepository
                .findByUsuarioIdUsuarioOrderByFechaInscripcionDesc(idUsuario);
    }

    @Transactional(readOnly = true)
    public Voluntariado obtenerVoluntariado(Integer idVoluntariado) {

        return voluntariadoRepository.findById(idVoluntariado)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Voluntariado no encontrado: " + idVoluntariado));
    }

    // HU-5 - La retroalimentación se realiza después del voluntariado
    @Transactional(readOnly = true)
    public boolean puedeEnviarRetroalimentacion(Integer idVoluntariado) {

        Voluntariado voluntariado =
                obtenerVoluntariado(idVoluntariado);

        if (voluntariado.getActividad() == null
                || voluntariado.getActividad().getFechaHoraInicio() == null) {
            return false;
        }

        LocalDateTime ahora = LocalDateTime.now();

        return !ahora.isBefore(
                voluntariado.getActividad().getFechaHoraInicio());
    }

    @Transactional
    public Voluntariado save(@Valid Voluntariado voluntariado) {
        return voluntariadoRepository.save(voluntariado);
    }

    @Transactional
    public void delete(Integer idVoluntariado) {

        if (!voluntariadoRepository.existsById(idVoluntariado)) {
            throw new IllegalArgumentException(
                    "El voluntariado con ID "
                    + idVoluntariado
                    + " no existe.");
        }

        try {

            voluntariadoRepository.deleteById(idVoluntariado);

        } catch (DataIntegrityViolationException e) {

            throw new IllegalStateException(
                    "No se puede eliminar el voluntariado. "
                    + "Tiene datos asociados.",
                    e);
        }
    }
}