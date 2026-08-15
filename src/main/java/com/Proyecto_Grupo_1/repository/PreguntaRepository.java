package com.Proyecto_Grupo_1.repository;

import com.Proyecto_Grupo_1.domain.Pregunta;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PreguntaRepository
        extends JpaRepository<Pregunta, Integer> {

    List<Pregunta> findAllByOrderByIdPreguntaAsc();
}