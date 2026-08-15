package com.Proyecto_Grupo_1.service;

import com.Proyecto_Grupo_1.domain.Usuario;
import com.Proyecto_Grupo_1.repository.UsuarioRepository;
import com.Proyecto_Grupo_1.repository.UsuarioRolRepository;
import java.util.Locale;
import java.util.stream.Collectors;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("userDetailsService")
public class UsuarioDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioRolRepository usuarioRolRepository;

    public UsuarioDetailsService(UsuarioRepository usuarioRepository,
            UsuarioRolRepository usuarioRolRepository) {
        this.usuarioRepository = usuarioRepository;
        this.usuarioRolRepository = usuarioRolRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByUsernameAndEstado_NombreEstado(username, "Activo")
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));

        var roles = usuarioRolRepository.findByUsuarioIdUsuario(usuario.getIdUsuario()).stream()
                .map(usuarioRol -> usuarioRol.getRol())
                .filter(rol -> rol != null && rol.getRol() != null && !rol.getRol().isBlank())
                .map(rol -> new SimpleGrantedAuthority(normalizarAuthority(rol.getRol())))
                .collect(Collectors.toSet());

        return new User(usuario.getUsername(), usuario.getPassword(), roles);
    }

    private String normalizarAuthority(String rol) {
        return "ROLE_" + rol.trim().toUpperCase(Locale.ROOT);
    }
}
