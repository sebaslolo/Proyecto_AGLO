package com.Proyecto_Grupo_1.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.Proyecto_Grupo_1.domain.Rol;
import com.Proyecto_Grupo_1.domain.Usuario;
import com.Proyecto_Grupo_1.domain.UsuarioRol;
import com.Proyecto_Grupo_1.repository.UsuarioRepository;
import com.Proyecto_Grupo_1.repository.UsuarioRolRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@ExtendWith(MockitoExtension.class)
class UsuarioDetailsServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private UsuarioRolRepository usuarioRolRepository;

    @Test
    void normalizaLosRolesALaConvencionRole() {
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(9);
        usuario.setUsername("maria");
        usuario.setPassword("$2a$10$hash");

        Rol admin = new Rol();
        admin.setRol("admin");
        UsuarioRol asignacionAdmin = new UsuarioRol();
        asignacionAdmin.setRol(admin);

        Rol cliente = new Rol();
        cliente.setRol("cliente");
        UsuarioRol asignacionCliente = new UsuarioRol();
        asignacionCliente.setRol(cliente);

        when(usuarioRepository.findByUsernameAndEstado_NombreEstado("maria", "Activo"))
                .thenReturn(Optional.of(usuario));
        when(usuarioRolRepository.findByUsuarioIdUsuario(9))
                .thenReturn(List.of(asignacionAdmin, asignacionCliente));

        var userDetails = new UsuarioDetailsService(usuarioRepository, usuarioRolRepository)
                .loadUserByUsername("maria");

        assertThat(userDetails.getAuthorities())
                .extracting(authority -> authority.getAuthority())
                .containsExactlyInAnyOrder("ROLE_ADMIN", "ROLE_CLIENTE");
    }

    @Test
    void rechazaUsuariosInactivosOInexistentes() {
        when(usuarioRepository.findByUsernameAndEstado_NombreEstado("inactivo", "Activo"))
                .thenReturn(Optional.empty());

        UsuarioDetailsService service = new UsuarioDetailsService(usuarioRepository, usuarioRolRepository);

        assertThatThrownBy(() -> service.loadUserByUsername("inactivo"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("inactivo");
    }
}
