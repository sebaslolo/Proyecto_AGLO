package com.Proyecto_Grupo_1.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.Proyecto_Grupo_1.domain.Estado;
import com.Proyecto_Grupo_1.domain.Rol;
import com.Proyecto_Grupo_1.domain.Usuario;
import com.Proyecto_Grupo_1.domain.UsuarioRol;
import com.Proyecto_Grupo_1.dto.RegistroForm;
import com.Proyecto_Grupo_1.repository.EstadoRepository;
import com.Proyecto_Grupo_1.repository.RolRepository;
import com.Proyecto_Grupo_1.repository.UsuarioRepository;
import com.Proyecto_Grupo_1.repository.UsuarioRolRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private EstadoRepository estadoRepository;

    @Mock
    private RolRepository rolRepository;

    @Mock
    private UsuarioRolRepository usuarioRolRepository;

    private PasswordEncoder passwordEncoder;
    private UsuarioService usuarioService;

    @BeforeEach
    void setUp() {
        passwordEncoder = new BCryptPasswordEncoder();
        usuarioService = new UsuarioService(
                usuarioRepository,
                estadoRepository,
                rolRepository,
                usuarioRolRepository,
                passwordEncoder);
    }

    @Test
    void registraClienteConBcryptYRolEnLaMismaOperacion() {
        Estado activo = new Estado();
        activo.setIdEstado(1);
        Rol cliente = new Rol();
        cliente.setIdRol(3);
        cliente.setRol("CLIENTE");
        RegistroForm form = registroValido();

        when(estadoRepository.findByNombreEstadoIgnoreCase("Activo")).thenReturn(Optional.of(activo));
        when(rolRepository.findByRolIgnoreCase("CLIENTE")).thenReturn(Optional.of(cliente));
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> {
            Usuario usuario = invocation.getArgument(0);
            usuario.setIdUsuario(27);
            return usuario;
        });

        Usuario guardado = usuarioService.registrarCliente(form);

        assertThat(guardado.getPassword()).isNotEqualTo(form.getPassword());
        assertThat(passwordEncoder.matches(form.getPassword(), guardado.getPassword())).isTrue();
        ArgumentCaptor<UsuarioRol> asignacion = ArgumentCaptor.forClass(UsuarioRol.class);
        verify(usuarioRolRepository).save(asignacion.capture());
        assertThat(asignacion.getValue().getId().getIdUsuario()).isEqualTo(27);
        assertThat(asignacion.getValue().getId().getIdRol()).isEqualTo(3);
        assertThat(asignacion.getValue().getRol()).isSameAs(cliente);
    }

    @Test
    void noCreaUsuarioSiFaltaElRolClienteRequerido() {
        Estado activo = new Estado();
        when(estadoRepository.findByNombreEstadoIgnoreCase("Activo")).thenReturn(Optional.of(activo));
        when(rolRepository.findByRolIgnoreCase("CLIENTE")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> usuarioService.registrarCliente(registroValido()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("CLIENTE");

        verify(usuarioRepository, never()).save(any(Usuario.class));
        verify(usuarioRolRepository, never()).save(any(UsuarioRol.class));
    }

    @Test
    void exigeDigitoEnLasContrasenasAdministrativas() {
        Usuario usuario = new Usuario();
        usuario.setPassword("sololetras");

        assertThatThrownBy(() -> usuarioService.save(usuario))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("dígito");

        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    private RegistroForm registroValido() {
        RegistroForm form = new RegistroForm();
        form.setUsername("cliente.nuevo");
        form.setNombre("Cliente");
        form.setApellidoPaterno("Nuevo");
        form.setCorreo("cliente.nuevo@aglo.test");
        form.setPassword("Clave123");
        form.setConfirmar("Clave123");
        return form;
    }
}
