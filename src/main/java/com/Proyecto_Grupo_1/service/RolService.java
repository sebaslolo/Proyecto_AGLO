package com.Proyecto_Grupo_1.service;

import com.Proyecto_Grupo_1.domain.Rol;
import com.Proyecto_Grupo_1.repository.RolRepository;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
public class RolService {

    private static final Set<String> ROLES_SISTEMA = Set.of("ADMIN", "GUIA", "CLIENTE");

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
    public Optional<Rol> getRolPorNombre(String rol) {
        return rolRepository.findByRolIgnoreCase(rol);
    }

    @Transactional(readOnly = true)
    public Rol obtenerRol(Integer idRol) {
        return rolRepository.findById(idRol)
                .orElseThrow(() -> new IllegalArgumentException("Rol no encontrado: " + idRol));
    }

    @Transactional
    public Rol save(@Valid Rol rol) {
        String nombreNormalizado = normalizarNombre(rol.getRol());
        if (rol.getIdRol() != null) {
            Rol existente = obtenerRol(rol.getIdRol());
            String nombreActual = normalizarNombre(existente.getRol());
            if (ROLES_SISTEMA.contains(nombreActual) && !nombreActual.equals(nombreNormalizado)) {
                throw new IllegalStateException("No se puede renombrar el rol de sistema " + nombreActual + ".");
            }
        }
        rol.setRol(nombreNormalizado);
        return rolRepository.save(rol);
    }

    @Transactional
    public void delete(Integer idRol) {
        Rol rol = obtenerRol(idRol);
        String nombreRol = normalizarNombre(rol.getRol());
        if (ROLES_SISTEMA.contains(nombreRol)) {
            throw new IllegalStateException("No se puede eliminar el rol de sistema " + nombreRol + ".");
        }
        try {
            rolRepository.deleteById(idRol);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException("No se puede eliminar el rol. Tiene datos asociados.", e);
        }
    }

    private String normalizarNombre(String rol) {
        if (rol == null || rol.isBlank()) {
            throw new IllegalArgumentException("El nombre del rol es obligatorio.");
        }
        String nombreNormalizado = rol.trim().toUpperCase(Locale.ROOT);
        if (nombreNormalizado.startsWith("ROLE_")) {
            throw new IllegalArgumentException("Los nombres de rol no deben incluir el prefijo ROLE_.");
        }
        return nombreNormalizado;
    }
}
