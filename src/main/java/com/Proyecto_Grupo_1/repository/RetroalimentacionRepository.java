package com.Proyecto_Grupo_1.repository;

import com.Proyecto_Grupo_1.domain.Retroalimentacion;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RetroalimentacionRepository extends JpaRepository<Retroalimentacion, Integer> {

    List<Retroalimentacion> findByUsuarioIdUsuario(Integer idUsuario);

    List<Retroalimentacion> findByVoluntariadoIdVoluntariadoRegistro(Integer idVoluntariado);

    boolean existsByUsuarioIdUsuarioAndVoluntariadoIdVoluntariadoRegistro(
            Integer idUsuario, Integer idVoluntariado);
}