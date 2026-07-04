package com.Proyecto_Grupo_1.repository;

import com.Proyecto_Grupo_1.domain.UsuarioRol;
import com.Proyecto_Grupo_1.domain.UsuarioRolId;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRolRepository extends JpaRepository<UsuarioRol, UsuarioRolId> {

    List<UsuarioRol> findByUsuarioIdUsuario(Integer idUsuario);

    List<UsuarioRol> findByRolIdRol(Integer idRol);
}
