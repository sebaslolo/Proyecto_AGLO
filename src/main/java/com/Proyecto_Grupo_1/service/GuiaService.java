package com.Proyecto_Grupo_1.service;

import com.Proyecto_Grupo_1.domain.Guia;
import com.Proyecto_Grupo_1.repository.GuiaRepository;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
public class GuiaService {

    private final GuiaRepository guiaRepository;

    public GuiaService(GuiaRepository guiaRepository) {
        this.guiaRepository = guiaRepository;
    }

    @Transactional(readOnly = true)
    public List<Guia> getGuias(boolean sinFiltro) {
        return guiaRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Guia> getGuia(Integer idGuia) {
        return guiaRepository.findById(idGuia);
    }

    @Transactional(readOnly = true)
    public Optional<Guia> getGuiaPorUsuario(Integer idUsuario) {
        return guiaRepository.findByUsuarioIdUsuario(idUsuario);
    }

    @Transactional(readOnly = true)
    public Guia obtenerGuia(Integer idGuia) {
        return guiaRepository.findById(idGuia)
                .orElseThrow(() -> new IllegalArgumentException("Guia no encontrado: " + idGuia));
    }

    @Transactional
    public Guia save(@Valid Guia guia) {
        return guiaRepository.save(guia);
    }

    @Transactional
    public void delete(Integer idGuia) {
        if (!guiaRepository.existsById(idGuia)) {
            throw new IllegalArgumentException("El guia con ID " + idGuia + " no existe.");
        }
        try {
            guiaRepository.deleteById(idGuia);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException("No se puede eliminar el guia. Tiene datos asociados.", e);
        }
    }
}
