package com.Proyecto_Grupo_1.repository;

import com.Proyecto_Grupo_1.domain.UsuarioRol;
import com.Proyecto_Grupo_1.domain.UsuarioRolId;
import com.Proyecto_Grupo_1.domain.Usuario;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UsuarioRolRepository extends JpaRepository<UsuarioRol, UsuarioRolId> {

    List<UsuarioRol> findByUsuarioIdUsuario(Integer idUsuario);

    List<UsuarioRol> findByRolIdRol(Integer idRol);

    @Query("""
            select distinct usuarioRol.usuario
            from UsuarioRol usuarioRol
            where upper(usuarioRol.rol.rol) in ('ADMIN', 'GUIA')
            order by usuarioRol.usuario.nombre, usuarioRol.usuario.apellidoPaterno,
                     usuarioRol.usuario.apellidoMaterno, usuarioRol.usuario.username
            """)
    List<Usuario> findUsuariosAsignablesAPrestamo();

    @Query("""
            select count(usuarioRol) > 0
            from UsuarioRol usuarioRol
            where usuarioRol.usuario.idUsuario = :idUsuario
              and upper(usuarioRol.rol.rol) in ('ADMIN', 'GUIA')
            """)
    boolean existsUsuarioAsignableAPrestamo(@Param("idUsuario") Integer idUsuario);

    @Query("""
            select distinct usuarioRol.usuario
            from UsuarioRol usuarioRol
            where upper(usuarioRol.rol.rol) = 'CLIENTE'
              and upper(usuarioRol.usuario.estado.nombreEstado) = 'ACTIVO'
            order by usuarioRol.usuario.nombre, usuarioRol.usuario.apellidoPaterno,
                     usuarioRol.usuario.apellidoMaterno, usuarioRol.usuario.username
            """)
    List<Usuario> findClientesActivosParaReservacion();

    @Query("""
            select count(usuarioRol) > 0
            from UsuarioRol usuarioRol
            where usuarioRol.usuario.idUsuario = :idUsuario
              and upper(usuarioRol.rol.rol) = 'CLIENTE'
              and upper(usuarioRol.usuario.estado.nombreEstado) = 'ACTIVO'
            """)
    boolean existsClienteActivoParaReservacion(@Param("idUsuario") Integer idUsuario);
}
