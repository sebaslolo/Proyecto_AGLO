package com.Proyecto_Grupo_1.service;

import com.Proyecto_Grupo_1.domain.Estado;
import com.Proyecto_Grupo_1.repository.EstadoRepository;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
public class EstadoService {

    private final EstadoRepository estadoRepository;

    public EstadoService(EstadoRepository estadoRepository) {
        this.estadoRepository = estadoRepository;
    }

    @Transactional(readOnly = true)
    public List<Estado> getEstados(boolean sinFiltro) {
        return estadoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Estado> getEstado(Integer idEstado) {
        return estadoRepository.findById(idEstado);
    }

    @Transactional(readOnly = true)
    public Estado obtenerEstado(Integer idEstado) {
        return estadoRepository.findById(idEstado)
                .orElseThrow(() -> new IllegalArgumentException("Estado no encontrado: " + idEstado));
    }

    @Transactional
    public Estado save(@Valid Estado estado) {
        return estadoRepository.save(estado);
    }

    @Transactional
    public void delete(Integer idEstado) {
        if (!estadoRepository.existsById(idEstado)) {
            throw new IllegalArgumentException("El estado con ID " + idEstado + " no existe.");
        }
        try {
            estadoRepository.deleteById(idEstado);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException("No se puede eliminar el estado. Tiene datos asociados.", e);
        }
    }
}
