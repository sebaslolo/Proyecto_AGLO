package com.Proyecto_Grupo_1.repository;

import com.Proyecto_Grupo_1.domain.Guia;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GuiaRepository extends JpaRepository<Guia, Integer> {

    Optional<Guia> findByUsuarioIdUsuario(Integer idUsuario);
}
