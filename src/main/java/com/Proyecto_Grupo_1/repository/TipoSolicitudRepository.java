package com.Proyecto_Grupo_1.repository;

import com.Proyecto_Grupo_1.domain.TipoSolicitud;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TipoSolicitudRepository
        extends JpaRepository<TipoSolicitud, Integer> {

    Optional<TipoSolicitud> findByNombreIgnoreCase(String nombre);
}