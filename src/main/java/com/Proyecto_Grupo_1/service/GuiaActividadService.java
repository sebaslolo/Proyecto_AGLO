package com.Proyecto_Grupo_1.service;

import com.Proyecto_Grupo_1.domain.Actividad;
import com.Proyecto_Grupo_1.domain.ActividadGuia;
import com.Proyecto_Grupo_1.domain.ActividadGuiaId;
import com.Proyecto_Grupo_1.domain.Estado;
import com.Proyecto_Grupo_1.domain.Usuario;
import com.Proyecto_Grupo_1.repository.ActividadGuiaRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
@RequiredArgsConstructor
public class GuiaActividadService {

    private final ActividadGuiaRepository actividadGuiaRepository;
    private final ActividadService actividadService;
    private final UsuarioService usuarioService;

    @Transactional(readOnly = true)
    public List<ActividadGuia> getAsignacionesPorActividad(Integer idActividad) {
        return actividadGuiaRepository.findByActividadIdActividad(idActividad);
    }

    @Transactional(readOnly = true)
    public List<ActividadGuia> getAsignacionesPorGuia(Integer idUsuario) {
        return actividadGuiaRepository.findByGuiaIdUsuario(idUsuario);
    }

    @Transactional(readOnly = true)
    public Optional<ActividadGuia> getAsignacion(Integer idActividad, Integer idUsuario) {
        return actividadGuiaRepository.findById(new ActividadGuiaId(idActividad, idUsuario));
    }

    @Transactional
    public ActividadGuia save(@NotNull Integer idActividad, @NotNull Integer idUsuario, @Valid @NotNull Estado estado) {
        return asignarGuia(idActividad, idUsuario, estado);
    }

    @Transactional
    public ActividadGuia asignarGuia(@NotNull Integer idActividad, @NotNull Integer idUsuario, @Valid @NotNull Estado estado) {
        ActividadGuiaId id = new ActividadGuiaId(idActividad, idUsuario);
        if (actividadGuiaRepository.existsById(id)) {
            throw new IllegalArgumentException("El guia ya esta asignado a esta actividad");
        }

        Actividad actividad = actividadService.obtenerActividad(idActividad);
        Usuario guia = usuarioService.obtenerUsuario(idUsuario);

        ActividadGuia actividadGuia = new ActividadGuia();
        actividadGuia.setId(id);
        actividadGuia.setActividad(actividad);
        actividadGuia.setGuia(guia);
        actividadGuia.setEstado(estado);
        return actividadGuiaRepository.save(actividadGuia);
    }

    @Transactional
    public void delete(Integer idActividad, Integer idUsuario) {
        ActividadGuiaId id = new ActividadGuiaId(idActividad, idUsuario);
        if (!actividadGuiaRepository.existsById(id)) {
            throw new IllegalArgumentException("La asignacion de guia no existe.");
        }
        try {
            actividadGuiaRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException("No se puede eliminar la asignacion. Tiene datos asociados.", e);
        }
    }

    @Transactional
    public void removerGuia(Integer idActividad, Integer idUsuario) {
        delete(idActividad, idUsuario);
    }

    @Transactional(readOnly = true)
    public List<ActividadGuia> listarGuiasPorActividad(Integer idActividad) {
        return getAsignacionesPorActividad(idActividad);
    }

    @Transactional(readOnly = true)
    public List<ActividadGuia> listarActividadesPorGuia(Integer idUsuario) {
        return getAsignacionesPorGuia(idUsuario);
    }
}
