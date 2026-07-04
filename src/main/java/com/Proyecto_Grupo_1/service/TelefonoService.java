package com.Proyecto_Grupo_1.service;

import com.Proyecto_Grupo_1.domain.Telefono;
import com.Proyecto_Grupo_1.repository.TelefonoRepository;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
public class TelefonoService {

    private final TelefonoRepository telefonoRepository;

    public TelefonoService(TelefonoRepository telefonoRepository) {
        this.telefonoRepository = telefonoRepository;
    }

    @Transactional(readOnly = true)
    public List<Telefono> getTelefonos(boolean sinFiltro) {
        return telefonoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Telefono> getTelefono(Integer idTelefono) {
        return telefonoRepository.findById(idTelefono);
    }

    @Transactional
    public Telefono save(@Valid Telefono telefono) {
        return telefonoRepository.save(telefono);
    }

    @Transactional
    public void delete(Integer idTelefono) {
        if (!telefonoRepository.existsById(idTelefono)) {
            throw new IllegalArgumentException("El telefono con ID " + idTelefono + " no existe.");
        }
        try {
            telefonoRepository.deleteById(idTelefono);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException("No se puede eliminar el telefono. Tiene datos asociados.", e);
        }
    }
}
