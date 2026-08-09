package com.Proyecto_Grupo_1.service;

import com.Proyecto_Grupo_1.domain.Retroalimentacion;
import com.Proyecto_Grupo_1.repository.RetroalimentacionRepository;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
public class RetroalimentacionService {

    private final RetroalimentacionRepository retroalimentacionRepository;

    public RetroalimentacionService(
            RetroalimentacionRepository retroalimentacionRepository) {

        this.retroalimentacionRepository = retroalimentacionRepository;
    }

    @Transactional(readOnly = true)
    public List<Retroalimentacion> getRetroalimentaciones(boolean sinFiltro) {
        return retroalimentacionRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Retroalimentacion> getRetroalimentacion(
            Integer idRetroalimentacion) {

        return retroalimentacionRepository.findById(idRetroalimentacion);
    }

    @Transactional(readOnly = true)
    public List<Retroalimentacion> getRetroalimentacionesPorUsuario(
            Integer idUsuario) {

        return retroalimentacionRepository
                .findByUsuarioIdUsuario(idUsuario);
    }

    @Transactional(readOnly = true)
    public List<Retroalimentacion> getRetroalimentacionesPorVoluntariado(
            Integer idVoluntariado) {

        return retroalimentacionRepository
                .findByVoluntariadoIdVoluntariado(idVoluntariado);
    }

    @Transactional(readOnly = true)
    public boolean yaEnvioRetroalimentacion(
            Integer idUsuario,
            Integer idVoluntariado) {

        return retroalimentacionRepository
                .existsByUsuarioIdUsuarioAndVoluntariadoIdVoluntariado(
                        idUsuario,
                        idVoluntariado);
    }

    @Transactional
    public Retroalimentacion save(
            @Valid Retroalimentacion retroalimentacion) {

        // HU-5 - La retroalimentación no puede editarse una vez enviada
        if (retroalimentacion.getIdRetroalimentacion() != null) {

            throw new IllegalStateException(
                    "La retroalimentación no puede editarse una vez enviada.");
        }

        // HU-5 - Verificar que no exista retroalimentación previa
        if (yaEnvioRetroalimentacion(
                retroalimentacion.getUsuario().getIdUsuario(),
                retroalimentacion.getVoluntariado().getIdVoluntariado())) {

            throw new IllegalStateException(
                    "Ya enviaste retroalimentación para este voluntariado.");
        }

        return retroalimentacionRepository.save(retroalimentacion);
    }

    @Transactional
    public void delete(Integer idRetroalimentacion) {

        if (!retroalimentacionRepository.existsById(idRetroalimentacion)) {

            throw new IllegalArgumentException(
                    "La retroalimentación con ID "
                    + idRetroalimentacion
                    + " no existe.");
        }

        try {

            retroalimentacionRepository.deleteById(idRetroalimentacion);

        } catch (DataIntegrityViolationException e) {

            throw new IllegalStateException(
                    "No se puede eliminar la retroalimentación. "
                    + "Tiene datos asociados.",
                    e);
        }
    }
}