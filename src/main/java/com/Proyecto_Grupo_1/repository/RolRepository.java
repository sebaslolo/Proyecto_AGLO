package com.Proyecto_Grupo_1.repository;

import com.Proyecto_Grupo_1.domain.Rol;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RolRepository extends JpaRepository<Rol, Integer> {

    Optional<Rol> findByRolIgnoreCase(String rol);
}
