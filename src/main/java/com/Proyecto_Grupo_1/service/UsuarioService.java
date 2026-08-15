package com.Proyecto_Grupo_1.service;

import com.Proyecto_Grupo_1.domain.Estado;
import com.Proyecto_Grupo_1.domain.Rol;
import com.Proyecto_Grupo_1.domain.Usuario;
import com.Proyecto_Grupo_1.domain.UsuarioRol;
import com.Proyecto_Grupo_1.domain.UsuarioRolId;
import com.Proyecto_Grupo_1.dto.RegistroForm;
import com.Proyecto_Grupo_1.repository.EstadoRepository;
import com.Proyecto_Grupo_1.repository.RolRepository;
import com.Proyecto_Grupo_1.repository.UsuarioRepository;
import com.Proyecto_Grupo_1.repository.UsuarioRolRepository;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.security.crypto.password.PasswordEncoder;

@Service
@Validated
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final EstadoRepository estadoRepository;
    private final RolRepository rolRepository;
    private final UsuarioRolRepository usuarioRolRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository,
            EstadoRepository estadoRepository,
            RolRepository rolRepository,
            UsuarioRolRepository usuarioRolRepository,
            PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.estadoRepository = estadoRepository;
        this.rolRepository = rolRepository;
        this.usuarioRolRepository = usuarioRolRepository;
        this.passwordEncoder = passwordEncoder;
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
    public Optional<Usuario> getUsuarioPorUsername(String username) {
        if (username == null || username.isBlank()) {
            return Optional.empty();
        }
        return usuarioRepository.findByUsernameIgnoreCase(username.trim());
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

    /**
     * Usuarios que pueden recibir una herramienta en préstamo. Los perfiles de
     * cliente no forman parte de este catálogo operativo.
     */
    @Transactional(readOnly = true)
    public List<Usuario> listarUsuariosAsignablesAPrestamo() {
        return usuarioRolRepository.findUsuariosAsignablesAPrestamo();
    }

    /**
     * Resuelve un usuario permitido para préstamo, evitando que un identificador
     * enviado manualmente desde el formulario eluda el catálogo mostrado.
     */
    @Transactional(readOnly = true)
    public Usuario obtenerUsuarioAsignableAPrestamo(Integer idUsuario) {
        Usuario usuario = obtenerUsuario(idUsuario);
        if (!usuarioRolRepository.existsUsuarioAsignableAPrestamo(idUsuario)) {
            throw new IllegalArgumentException("El usuario seleccionado debe tener rol ADMIN o GUIA.");
        }
        return usuario;
    }

    /**
     * Catálogo seguro para las altas administrativas de reservaciones. Sólo los
     * usuarios con rol CLIENTE y estado Activo pueden ser seleccionados.
     */
    @Transactional(readOnly = true)
    public List<Usuario> listarClientesActivosParaReservacion() {
        return usuarioRolRepository.findClientesActivosParaReservacion();
    }

    /**
     * Valida nuevamente el cliente en el servidor para que un identificador
     * manipulado no pueda reservar a nombre de una cuenta inactiva o sin rol
     * CLIENTE.
     */
    @Transactional(readOnly = true)
    public Usuario obtenerClienteActivoParaReservacion(Integer idUsuario) {
        if (idUsuario == null) {
            throw new IllegalArgumentException("Debe seleccionar un cliente activo válido.");
        }
        Usuario usuario = obtenerUsuario(idUsuario);
        if (!usuarioRolRepository.existsClienteActivoParaReservacion(idUsuario)) {
            throw new IllegalArgumentException("El usuario seleccionado debe tener rol CLIENTE y estar activo.");
        }
        return usuario;
    }

    @Transactional
    public Usuario save(@Valid Usuario usuario) {
        if (usuario.getIdUsuario() != null) {
            Usuario existente = obtenerUsuario(usuario.getIdUsuario());
            if (usuario.getPassword() == null || usuario.getPassword().isBlank()) {
                usuario.setPassword(existente.getPassword());
            } else {
                validarPassword(usuario.getPassword());
                usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
            }
        } else {
            validarPassword(usuario.getPassword());
            usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        }
        return usuarioRepository.save(usuario);
    }

    @Transactional
    public Usuario registrarCliente(RegistroForm registroForm) {
        if (existeUsername(registroForm.getUsername())) {
            throw new IllegalArgumentException("El usuario ya existe.");
        }
        if (existeCorreo(registroForm.getCorreo())) {
            throw new IllegalArgumentException("El correo ya existe.");
        }
        validarPassword(registroForm.getPassword());

        Estado estadoActivo = estadoRepository.findByNombreEstadoIgnoreCase("Activo")
                .orElseThrow(() -> new IllegalStateException("No existe el estado Activo requerido para registrar usuarios."));
        Rol rolCliente = rolRepository.findByRolIgnoreCase("CLIENTE")
                .orElseThrow(() -> new IllegalStateException("No existe el rol CLIENTE requerido para registrar usuarios."));

        Usuario usuario = new Usuario();
        usuario.setUsername(registroForm.getUsername().trim());
        usuario.setNombre(registroForm.getNombre().trim());
        usuario.setApellidoPaterno(registroForm.getApellidoPaterno().trim());
        usuario.setApellidoMaterno(limpiarOpcional(registroForm.getApellidoMaterno()));
        usuario.setCorreo(registroForm.getCorreo().trim());
        usuario.setTelefono(limpiarOpcional(registroForm.getTelefono()));
        usuario.setPassword(passwordEncoder.encode(registroForm.getPassword()));
        usuario.setEstado(estadoActivo);
        Usuario guardado = usuarioRepository.save(usuario);

        UsuarioRol usuarioRol = new UsuarioRol();
        usuarioRol.setId(new UsuarioRolId(guardado.getIdUsuario(), rolCliente.getIdRol()));
        usuarioRol.setUsuario(guardado);
        usuarioRol.setRol(rolCliente);
        usuarioRolRepository.save(usuarioRol);
        return guardado;
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

    private String limpiarOpcional(String texto) {
        return texto == null || texto.isBlank() ? null : texto.trim();
    }

    private void validarPassword(String password) {
        if (!cumplePoliticaPassword(password)) {
            throw new IllegalArgumentException("La contraseña debe tener al menos 8 caracteres y un dígito.");
        }
    }

    /**
     * Exposes the server-side rule so MVC forms can present a binding error before
     * invoking {@link #save(com.Proyecto_Grupo_1.domain.Usuario)}.
     */
    public boolean cumplePoliticaPassword(String password) {
        return password != null
                && !password.isBlank()
                && password.length() >= 8
                && password.chars().anyMatch(Character::isDigit);
    }

}
