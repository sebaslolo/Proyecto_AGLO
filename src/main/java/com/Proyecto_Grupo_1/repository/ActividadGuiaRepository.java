package com.Proyecto_Grupo_1.repository;

import com.Proyecto_Grupo_1.domain.ActividadGuia;
import com.Proyecto_Grupo_1.domain.ActividadGuiaId;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActividadGuiaRepository extends JpaRepository<ActividadGuia, ActividadGuiaId> {

    List<ActividadGuia> findByActividadIdActividad(Integer idActividad);

    List<ActividadGuia> findByGuiaIdUsuario(Integer idUsuario);

    void deleteByActividadIdActividadAndGuiaIdUsuario(Integer idActividad, Integer idUsuario);
}
