package com.Proyecto_Grupo_1.repository;

import com.Proyecto_Grupo_1.domain.Usuario;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    Optional<Usuario> findByUsernameIgnoreCase(String username);

    List<Usuario> findByNombreContainingIgnoreCaseOrApellidoPaternoContainingIgnoreCase(
            String nombre,
            String apellidoPaterno);
}
