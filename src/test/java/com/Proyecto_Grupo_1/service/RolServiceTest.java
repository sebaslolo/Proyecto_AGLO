package com.Proyecto_Grupo_1.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.Proyecto_Grupo_1.domain.Rol;
import com.Proyecto_Grupo_1.repository.RolRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RolServiceTest {

    @Mock
    private RolRepository rolRepository;

    @Test
    void impideRenombrarUnRolDeSistema() {
        Rol persistido = rol(1, "ADMIN");
        Rol cambio = rol(1, "OPERADOR");
        when(rolRepository.findById(1)).thenReturn(Optional.of(persistido));

        assertThatThrownBy(() -> new RolService(rolRepository).save(cambio))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("ADMIN");

        verify(rolRepository, never()).save(any(Rol.class));
    }

    @Test
    void impideEliminarUnRolDeSistema() {
        when(rolRepository.findById(2)).thenReturn(Optional.of(rol(2, "GUIA")));

        assertThatThrownBy(() -> new RolService(rolRepository).delete(2))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("GUIA");

        verify(rolRepository, never()).deleteById(2);
    }

    @Test
    void permiteRolesAdicionalesSinCambiarSuNombreNormalizado() {
        Rol adicional = rol(null, " observador ");
        when(rolRepository.save(adicional)).thenReturn(adicional);

        Rol guardado = new RolService(rolRepository).save(adicional);

        assertThat(guardado.getRol()).isEqualTo("OBSERVADOR");
        verify(rolRepository).save(adicional);
    }

    @Test
    void rechazaElPrefijoReservadoRole() {
        Rol intento = rol(null, "ROLE_ADMIN");

        assertThatThrownBy(() -> new RolService(rolRepository).save(intento))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("ROLE_");

        verify(rolRepository, never()).save(any(Rol.class));
    }

    private Rol rol(Integer id, String nombre) {
        Rol rol = new Rol();
        rol.setIdRol(id);
        rol.setRol(nombre);
        return rol;
    }
}
