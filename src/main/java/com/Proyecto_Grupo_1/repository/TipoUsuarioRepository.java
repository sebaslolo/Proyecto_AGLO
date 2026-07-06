package com.Proyecto_Grupo_1.repository;

import com.Proyecto_Grupo_1.domain.TipoUsuario;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface TipoUsuarioRepository extends JpaRepository<TipoUsuario, Integer> {

    Optional<TipoUsuario> findByNombreTipoUsuarioIgnoreCase(String nombreTipoUsuario);
}
