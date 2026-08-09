package com.Proyecto_Grupo_1.repository;

import com.Proyecto_Grupo_1.domain.Voluntariado;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VoluntariadoRepository
        extends JpaRepository<Voluntariado, Integer> {

    Optional<Voluntariado> findByUsuarioIdUsuario(Integer idUsuario);

    boolean existsByUsuarioIdUsuario(Integer idUsuario);
}