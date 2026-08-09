package com.Proyecto_Grupo_1.service;

import com.Proyecto_Grupo_1.domain.Voluntariado;
import com.Proyecto_Grupo_1.repository.VoluntariadoRepository;
import jakarta.validation.Valid;
import java.time.LocalDate;
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

    public VoluntariadoService(VoluntariadoRepository voluntariadoRepository) {
        this.voluntariadoRepository = voluntariadoRepository;
    }

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
        return voluntariadoRepository.findByUsuarioIdUsuario(idUsuario);
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

        Voluntariado voluntariado = obtenerVoluntariado(idVoluntariado);

        if (voluntariado.getFechaSesion() == null) {
            return false;
        }

        LocalDate hoy = LocalDate.now();

        return !hoy.isBefore(voluntariado.getFechaSesion());
    }

    @Transactional
    public Voluntariado save(@Valid Voluntariado voluntariado) {
        return voluntariadoRepository.save(voluntariado);
    }

    @Transactional
    public void delete(Integer idVoluntariado) {

        if (!voluntariadoRepository.existsById(idVoluntariado)) {
            throw new IllegalArgumentException(
                    "El voluntariado con ID " + idVoluntariado + " no existe.");
        }

        try {
            voluntariadoRepository.deleteById(idVoluntariado);

        } catch (DataIntegrityViolationException e) {

            throw new IllegalStateException(
                    "No se puede eliminar el voluntariado. Tiene datos asociados.", e);
        }
    }
}