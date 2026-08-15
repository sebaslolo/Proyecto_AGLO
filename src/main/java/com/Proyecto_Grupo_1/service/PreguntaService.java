package com.Proyecto_Grupo_1.service;

import com.Proyecto_Grupo_1.domain.Pregunta;
import com.Proyecto_Grupo_1.repository.PreguntaRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PreguntaService {

    private final PreguntaRepository preguntaRepository;

    public PreguntaService(
            PreguntaRepository preguntaRepository) {

        this.preguntaRepository =
                preguntaRepository;
    }

    @Transactional(readOnly = true)
    public List<Pregunta> getPreguntas() {

        return preguntaRepository
                .findAllByOrderByIdPreguntaAsc();
    }

    @Transactional(readOnly = true)
    public Pregunta obtenerPregunta(
            Integer idPregunta) {

        return preguntaRepository
                .findById(idPregunta)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Pregunta no encontrada: "
                                + idPregunta));
    }
}