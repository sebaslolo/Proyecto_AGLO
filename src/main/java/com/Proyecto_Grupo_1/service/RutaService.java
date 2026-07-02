package com.Proyecto_Grupo_1.service;

import com.Proyecto_Grupo_1.domain.Ruta;
import com.Proyecto_Grupo_1.repository.RutaRepository;
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
public class RutaService {

    private final RutaRepository rutaRepository;

    @Transactional(readOnly = true)
    public List<Ruta> getRutas(boolean sinFiltro) {
        return rutaRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Ruta> getRuta(Integer idRuta) {
        return rutaRepository.findById(idRuta);
    }

    @Transactional
    public Ruta save(@Valid Ruta ruta) {
        return rutaRepository.save(ruta);
    }

    @Transactional
    public void delete(Integer idRuta) {
        if (!rutaRepository.existsById(idRuta)) {
            throw new IllegalArgumentException("La ruta con ID " + idRuta + " no existe.");
        }
        try {
            rutaRepository.deleteById(idRuta);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException("No se puede eliminar la ruta. Tiene datos asociados.", e);
        }
    }
}
