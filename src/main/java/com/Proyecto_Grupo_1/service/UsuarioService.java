package com.Proyecto_Grupo_1.service;

import com.Proyecto_Grupo_1.domain.Usuario;
import com.Proyecto_Grupo_1.repository.CuentaRepository;
import com.Proyecto_Grupo_1.repository.UsuarioRepository;
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
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final CuentaRepository cuentaRepository;

    @Transactional(readOnly = true)
    public List<Usuario> getUsuarios(boolean soloGuias) {
        if (soloGuias) {
            return usuarioRepository.findGuiasYColaboradores();
        }
        return usuarioRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Usuario> getUsuario(Integer idUsuario) {
        return usuarioRepository.findById(idUsuario);
    }

    @Transactional(readOnly = true)
    public List<Usuario> listarUsuarios() {
        return getUsuarios(false);
    }

    @Transactional(readOnly = true)
    public Usuario obtenerUsuario(Integer idUsuario) {
        return usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado: " + idUsuario));
    }

    @Transactional(readOnly = true)
    public List<Usuario> buscarUsuarios(String termino) {
        if (termino == null || termino.isBlank()) {
            return listarUsuarios();
        }
        return usuarioRepository.findByNombreContainingIgnoreCaseOrApellidoPaternoContainingIgnoreCase(
                termino,
                termino);
    }

    @Transactional(readOnly = true)
    public List<Usuario> listarGuias() {
        return getUsuarios(true);
    }

    @Transactional
    public Usuario save(@Valid Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    @Transactional
    public Usuario guardarUsuario(@Valid Usuario usuario) {
        return save(usuario);
    }

    @Transactional
    public void delete(Integer idUsuario) {
        if (!usuarioRepository.existsById(idUsuario)) {
            throw new IllegalArgumentException("El usuario con ID " + idUsuario + " no existe.");
        }
        try {
            usuarioRepository.deleteById(idUsuario);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException("No se puede eliminar el usuario. Tiene datos asociados.", e);
        }
    }

    @Transactional(readOnly = true)
    public boolean existeCorreo(String correoElectronico) {
        return cuentaRepository.existsByCorreoElectronicoIgnoreCase(correoElectronico);
    }
}
