package com.Proyecto_Grupo_1.repository;

import com.Proyecto_Grupo_1.domain.Respuesta;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RespuestaRepository
        extends JpaRepository<Respuesta, Integer> {

    List<Respuesta> findBySolicitudIdSolicitud(
            Integer idSolicitud);

    boolean existsBySolicitudIdSolicitudAndPreguntaIdPregunta(
            Integer idSolicitud,
            Integer idPregunta);
}