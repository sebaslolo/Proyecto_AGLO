package com.Proyecto_Grupo_1.service;

import com.Proyecto_Grupo_1.domain.TipoActividad;
import com.Proyecto_Grupo_1.repository.TipoActividadRepository;
import jakarta.validation.Valid;
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
public class TipoActividadService {

    private final TipoActividadRepository tipoActividadRepository;

    @Transactional(readOnly = true)
    public List<TipoActividad> getTiposActividad(boolean sinFiltro) {
        return tipoActividadRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<TipoActividad> getTipoActividad(Integer idTipoActividad) {
        return tipoActividadRepository.findById(idTipoActividad);
    }

    @Transactional
    public TipoActividad save(@Valid TipoActividad tipoActividad) {
        return tipoActividadRepository.save(tipoActividad);
    }

    @Transactional
    public void delete(Integer idTipoActividad) {
        if (!tipoActividadRepository.existsById(idTipoActividad)) {
            throw new IllegalArgumentException("El tipo de actividad con ID " + idTipoActividad + " no existe.");
        }
        try {
            tipoActividadRepository.deleteById(idTipoActividad);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException("No se puede eliminar el tipo de actividad. Tiene datos asociados.", e);
        }
    }
}
