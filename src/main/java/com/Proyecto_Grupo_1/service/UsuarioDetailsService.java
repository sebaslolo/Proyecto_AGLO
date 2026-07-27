package com.Proyecto_Grupo_1.service;

import com.Proyecto_Grupo_1.domain.Usuario;
import com.Proyecto_Grupo_1.repository.UsuarioRepository;
import com.Proyecto_Grupo_1.repository.UsuarioRolRepository;
import jakarta.servlet.http.HttpSession;
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
    private final HttpSession session;

    public UsuarioDetailsService(UsuarioRepository usuarioRepository,
            UsuarioRolRepository usuarioRolRepository,
            HttpSession session) {
        this.usuarioRepository = usuarioRepository;
        this.usuarioRolRepository = usuarioRolRepository;
        this.session = session;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByUsernameAndEstado_NombreEstado(username, "Activo")
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));

        session.removeAttribute("imagenUsuario");
        session.setAttribute("imagenUsuario", usuario.getRutaImagen());

        var roles = usuarioRolRepository.findByUsuarioIdUsuario(usuario.getIdUsuario()).stream()
                .map(usuarioRol -> new SimpleGrantedAuthority("ROLE_" + usuarioRol.getRol().getRol()))
                .collect(Collectors.toSet());

        return new User(usuario.getUsername(), usuario.getPassword(), roles);
    }
}
