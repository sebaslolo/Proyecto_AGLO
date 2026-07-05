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
    public Optional<Usuario> autenticar(String correo, String password) {
        return usuarioRepository.findByCorreoIgnoreCase(correo)
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
        if (existeCorreo(registroForm.getCorreo())) {
            throw new IllegalArgumentException("El correo ya existe.");
        }

        Usuario usuario = new Usuario();
        String[] partesNombre = separarNombre(registroForm.getNombreCompleto());
        usuario.setNombre(partesNombre[0]);
        usuario.setApellidoPaterno(partesNombre[1]);
        usuario.setCorreo(registroForm.getCorreo());
        usuario.setUsername(generarUsername(registroForm.getCorreo()));
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
        return correoElectronico != null && usuarioRepository.existsByCorreoIgnoreCase(correoElectronico);
    }

    @Transactional(readOnly = true)
    public boolean correoDisponible(String correoElectronico, Integer idUsuarioActual) {
        if (correoElectronico == null || correoElectronico.isBlank()) {
            return true;
        }
        return usuarioRepository.findByCorreoIgnoreCase(correoElectronico)
                .map(usuario -> usuario.getIdUsuario().equals(idUsuarioActual))
                .orElse(true);
    }

    private String generarUsername(String correoElectronico) {
        String base = correoElectronico;
        int arroba = correoElectronico.indexOf('@');
        if (arroba > 0) {
            base = correoElectronico.substring(0, arroba);
        }
        base = limpiarUsername(base);
        String candidato = limitar(base, 30);
        int consecutivo = 1;
        while (usuarioRepository.existsByUsernameIgnoreCase(candidato)) {
            String sufijo = String.valueOf(consecutivo++);
            candidato = limitar(base, 30 - sufijo.length()) + sufijo;
        }
        return candidato;
    }

    private String limpiarUsername(String texto) {
        String limpio = texto == null ? "usuario" : texto.replaceAll("[^A-Za-z0-9_]", "");
        return limpio.isBlank() ? "usuario" : limpio;
    }

    private String[] separarNombre(String nombreCompleto) {
        String limpio = nombreCompleto == null ? "" : nombreCompleto.trim().replaceAll("\\s+", " ");
        if (limpio.isBlank()) {
            return new String[]{"Cliente", "AGLO"};
        }
        String[] partes = limpio.split(" ", 2);
        String nombre = limitar(partes[0], 20);
        String apellido = partes.length > 1 ? partes[1] : "AGLO";
        return new String[]{nombre, limitar(apellido, 30)};
    }

    private String limitar(String texto, int longitudMaxima) {
        if (texto == null) {
            return "";
        }
        return texto.length() <= longitudMaxima ? texto : texto.substring(0, longitudMaxima);
    }
}
