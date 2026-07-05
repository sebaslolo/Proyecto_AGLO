package com.Proyecto_Grupo_1.repository;

import com.Proyecto_Grupo_1.domain.Usuario;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    Optional<Usuario> findByUsernameIgnoreCase(String username);

    Optional<Usuario> findByCorreoIgnoreCase(String correo);

    boolean existsByCorreoIgnoreCase(String correo);

    boolean existsByUsernameIgnoreCase(String username);

    List<Usuario> findByNombreContainingIgnoreCaseOrApellidoPaternoContainingIgnoreCaseOrCorreoContainingIgnoreCase(
            String nombre,
            String apellidoPaterno,
            String correo);
}
