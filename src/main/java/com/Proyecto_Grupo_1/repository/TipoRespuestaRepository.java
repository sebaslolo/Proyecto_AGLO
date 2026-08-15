package com.Proyecto_Grupo_1.repository;

import com.Proyecto_Grupo_1.domain.TipoRespuesta;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TipoRespuestaRepository
        extends JpaRepository<TipoRespuesta, Integer> {

    Optional<TipoRespuesta> findByNombreIgnoreCase(String nombre);
}