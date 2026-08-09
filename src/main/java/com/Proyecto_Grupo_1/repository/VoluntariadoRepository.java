package com.Proyecto_Grupo_1.repository;

import com.Proyecto_Grupo_1.domain.Voluntariado;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VoluntariadoRepository extends JpaRepository<Voluntariado, Integer> {

    boolean existsByUsuarioIdUsuarioAndActividadIdActividad(
            Integer idUsuario,
            Integer idActividad);

    long countByActividadIdActividad(Integer idActividad);

    List<Voluntariado> findByUsuarioIdUsuarioOrderByFechaInscripcionDesc(
            Integer idUsuario);
}