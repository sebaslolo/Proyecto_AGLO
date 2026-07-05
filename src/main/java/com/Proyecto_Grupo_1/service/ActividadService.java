package com.Proyecto_Grupo_1.service;

import com.Proyecto_Grupo_1.domain.Actividad;
import com.Proyecto_Grupo_1.repository.ActividadRepository;
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
public class ActividadService {

    private final ActividadRepository actividadRepository;

    public ActividadService(ActividadRepository actividadRepository) {
        this.actividadRepository = actividadRepository;
    }

    @Transactional(readOnly = true)
    public List<Actividad> getActividades(boolean futuras) {
        if (futuras) {
            return actividadRepository.findByFechaHoraInicioAfterOrderByFechaHoraInicioAsc(LocalDateTime.now());
        }
        return actividadRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Actividad> getActividad(Integer idActividad) {
        return actividadRepository.findById(idActividad);
    }

    @Transactional(readOnly = true)
    public List<Actividad> listarActividades() {
        return getActividades(false);
    }

    @Transactional(readOnly = true)
    public List<Actividad> listarActividadesFuturas() {
        return getActividades(true);
    }

    @Transactional(readOnly = true)
    public List<Actividad> buscarActividades(String termino) {
        if (termino == null || termino.isBlank()) {
            return listarActividades();
        }
        return actividadRepository.findByNombreActividadContainingIgnoreCase(termino);
    }

    @Transactional(readOnly = true)
    public Actividad obtenerActividad(Integer idActividad) {
        return actividadRepository.findById(idActividad)
                .orElseThrow(() -> new IllegalArgumentException("Actividad no encontrada: " + idActividad));
    }

    @Transactional
    public Actividad save(@Valid Actividad actividad) {
        if (actividad.getFechaHoraInicio() != null && actividad.getFechaHoraInicio().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("La fecha de inicio no puede ser anterior a la fecha actual");
        }
        return actividadRepository.save(actividad);
    }

    @Transactional
    public Actividad guardarActividad(@Valid Actividad actividad) {
        return save(actividad);
    }

    @Transactional
    public void delete(Integer idActividad) {
        if (!actividadRepository.existsById(idActividad)) {
            throw new IllegalArgumentException("La actividad con ID " + idActividad + " no existe.");
        }
        try {
            actividadRepository.deleteById(idActividad);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException("No se puede eliminar la actividad. Tiene datos asociados.", e);
        }
    }

    @Transactional
    public void eliminarActividad(Integer idActividad) {
        delete(idActividad);
    }
}
