package com.Proyecto_Grupo_1.service;

import com.Proyecto_Grupo_1.domain.Rol;
import com.Proyecto_Grupo_1.domain.Usuario;
import com.Proyecto_Grupo_1.domain.UsuarioRol;
import com.Proyecto_Grupo_1.domain.UsuarioRolId;
import com.Proyecto_Grupo_1.repository.UsuarioRolRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UsuarioRolService {

    private final UsuarioRolRepository usuarioRolRepository;
    private final UsuarioService usuarioService;
    private final RolService rolService;

    @Transactional(readOnly = true)
    public List<UsuarioRol> getRolesPorUsuario(Integer idUsuario) {
        return usuarioRolRepository.findByUsuarioIdUsuario(idUsuario);
    }

    @Transactional(readOnly = true)
    public Optional<UsuarioRol> getUsuarioRol(Integer idUsuario, Integer idRol) {
        return usuarioRolRepository.findById(new UsuarioRolId(idUsuario, idRol));
    }

    @Transactional
    public UsuarioRol save(Integer idUsuario, Integer idRol) {
        UsuarioRolId id = new UsuarioRolId(idUsuario, idRol);
        if (usuarioRolRepository.existsById(id)) {
            throw new IllegalArgumentException("El usuario ya tiene este rol asignado.");
        }
        Usuario usuario = usuarioService.obtenerUsuario(idUsuario);
        Rol rol = rolService.obtenerRol(idRol);
        UsuarioRol usuarioRol = new UsuarioRol();
        usuarioRol.setId(id);
        usuarioRol.setUsuario(usuario);
        usuarioRol.setRol(rol);
        return usuarioRolRepository.save(usuarioRol);
    }

    @Transactional
    public void delete(Integer idUsuario, Integer idRol) {
        UsuarioRolId id = new UsuarioRolId(idUsuario, idRol);
        if (!usuarioRolRepository.existsById(id)) {
            throw new IllegalArgumentException("La asignacion de rol no existe.");
        }
        try {
            usuarioRolRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException("No se puede eliminar la asignacion. Tiene datos asociados.", e);
        }
    }
}
