package com.Proyecto_Grupo_1.repository;

import com.Proyecto_Grupo_1.domain.InscripcionVoluntariado;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InscripcionVoluntariadoRepository
        extends JpaRepository<InscripcionVoluntariado, Integer> {

    boolean existsByVoluntariadoUsuarioIdUsuarioAndActividadIdActividad(
            Integer idUsuario,
            Integer idActividad);

    long countByActividadIdActividad(Integer idActividad);

    List<InscripcionVoluntariado>
            findByVoluntariadoUsuarioIdUsuarioOrderByFechaInscripcionDesc(
                    Integer idUsuario);
}