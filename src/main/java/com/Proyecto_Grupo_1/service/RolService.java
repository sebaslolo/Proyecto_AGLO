package com.Proyecto_Grupo_1.service;

import com.Proyecto_Grupo_1.domain.Rol;
import com.Proyecto_Grupo_1.repository.RolRepository;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
public class RolService {

    private final RolRepository rolRepository;

    public RolService(RolRepository rolRepository) {
        this.rolRepository = rolRepository;
    }

    @Transactional(readOnly = true)
    public List<Rol> getRoles(boolean sinFiltro) {
        return rolRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Rol> getRol(Integer idRol) {
        return rolRepository.findById(idRol);
    }

    @Transactional(readOnly = true)
    public Rol obtenerRol(Integer idRol) {
        return rolRepository.findById(idRol)
                .orElseThrow(() -> new IllegalArgumentException("Rol no encontrado: " + idRol));
    }

    @Transactional
    public Rol save(@Valid Rol rol) {
        return rolRepository.save(rol);
    }

    @Transactional
    public void delete(Integer idRol) {
        if (!rolRepository.existsById(idRol)) {
            throw new IllegalArgumentException("El rol con ID " + idRol + " no existe.");
        }
        try {
            rolRepository.deleteById(idRol);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException("No se puede eliminar el rol. Tiene datos asociados.", e);
        }
    }
}
