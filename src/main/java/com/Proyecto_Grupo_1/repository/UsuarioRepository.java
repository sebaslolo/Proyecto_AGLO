package com.Proyecto_Grupo_1.repository;

import com.Proyecto_Grupo_1.domain.Usuario;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    Optional<Usuario> findByIdentificacion(String identificacion);

    List<Usuario> findByNombreContainingIgnoreCaseOrApellidoPaternoContainingIgnoreCase(
            String nombre,
            String apellidoPaterno);

    @Query("""
            select u
            from Usuario u
            join u.tipoUsuario tu
            where lower(tu.nombreTipoUsuario) like '%guia%'
               or lower(tu.nombreTipoUsuario) like '%guía%'
               or lower(tu.nombreTipoUsuario) like '%colaborador%'
            """)
    List<Usuario> findGuiasYColaboradores();
}
