package com.Proyecto_Grupo_1.service;

import com.Proyecto_Grupo_1.domain.Estado;
import com.Proyecto_Grupo_1.domain.Usuario;
import com.Proyecto_Grupo_1.dto.RegistroForm;
import com.Proyecto_Grupo_1.repository.UsuarioRepository;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional(readOnly = true)
    public List<Usuario> getUsuarios(boolean sinFiltro) {
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
    public Optional<Usuario> getUsuarioPorCorreo(String correo) {
        return usuarioRepository.findByCorreoIgnoreCase(correo);
    }

    @Transactional(readOnly = true)
    public Optional<Usuario> autenticar(String identificador, String password) {
        if (identificador == null || identificador.isBlank()) {
            return Optional.empty();
        }
        return buscarPorCorreoOUsername(identificador.trim())
                .filter(usuario -> password != null && password.equals(usuario.getPassword()));
    }

    @Transactional(readOnly = true)
    public List<Usuario> buscarUsuarios(String termino) {
        if (termino == null || termino.isBlank()) {
            return listarUsuarios();
        }
        return usuarioRepository.findByNombreContainingIgnoreCaseOrApellidoPaternoContainingIgnoreCaseOrCorreoContainingIgnoreCase(
                termino,
                termino,
                termino);
    }

    @Transactional(readOnly = true)
    public List<Usuario> listarGuias() {
        return getUsuarios(false);
    }

    @Transactional
    public Usuario save(@Valid Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    @Transactional
    public Usuario registrarCliente(RegistroForm registroForm, Estado estadoActivo) {
        if (existeUsername(registroForm.getUsername())) {
            throw new IllegalArgumentException("El usuario ya existe.");
        }
        if (existeCorreo(registroForm.getCorreo())) {
            throw new IllegalArgumentException("El correo ya existe.");
        }

        Usuario usuario = new Usuario();
        usuario.setUsername(registroForm.getUsername().trim());
        usuario.setNombre(registroForm.getNombre().trim());
        usuario.setApellidoPaterno(registroForm.getApellidoPaterno().trim());
        usuario.setApellidoMaterno(limpiarOpcional(registroForm.getApellidoMaterno()));
        usuario.setCorreo(registroForm.getCorreo().trim());
        usuario.setTelefono(limpiarOpcional(registroForm.getTelefono()));
        usuario.setPassword(registroForm.getPassword());
        usuario.setEstado(estadoActivo);
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
        return correoElectronico != null
                && !correoElectronico.isBlank()
                && usuarioRepository.existsByCorreoIgnoreCase(correoElectronico.trim());
    }

    @Transactional(readOnly = true)
    public boolean existeUsername(String username) {
        return username != null
                && !username.isBlank()
                && usuarioRepository.existsByUsernameIgnoreCase(username.trim());
    }

    @Transactional(readOnly = true)
    public boolean correoDisponible(String correoElectronico, Integer idUsuarioActual) {
        if (correoElectronico == null || correoElectronico.isBlank()) {
            return true;
        }
        return usuarioRepository.findByCorreoIgnoreCase(correoElectronico.trim())
                .map(usuario -> usuario.getIdUsuario().equals(idUsuarioActual))
                .orElse(true);
    }

    private Optional<Usuario> buscarPorCorreoOUsername(String identificador) {
        Optional<Usuario> porCorreo = usuarioRepository.findByCorreoIgnoreCase(identificador);
        if (porCorreo.isPresent()) {
            return porCorreo;
        }
        return usuarioRepository.findByUsernameIgnoreCase(identificador);
    }

    private String limpiarOpcional(String texto) {
        return texto == null || texto.isBlank() ? null : texto.trim();
    }
}
