package com.Proyecto_Grupo_1.service;

import com.Proyecto_Grupo_1.domain.Tortuga;
import com.Proyecto_Grupo_1.repository.TortugaRepository;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
public class TortugaService {

    private final TortugaRepository tortugaRepository;
    private final EstadoService estadoService;

    public TortugaService(TortugaRepository tortugaRepository,
            EstadoService estadoService) {
        this.tortugaRepository = tortugaRepository;
        this.estadoService = estadoService;
    }

    @Transactional(readOnly = true)
    public List<Tortuga> getTortugas(boolean sinFiltro){
        return tortugaRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Tortuga> getTortuga(String etiquetaTortuga){
        return tortugaRepository.findById(etiquetaTortuga);
    }

    @Transactional
    public Tortuga save(@Valid Tortuga tortuga){
        return save(tortuga, null);
    }

    /**
     * Persists only the fields exposed by the tortuga form.  The original label is
     * supplied by the edit form so a submitted primary-key change cannot be
     * interpreted as a new record.
     */
    @Transactional
    public Tortuga save(@Valid Tortuga tortuga, String etiquetaOriginal) {
        boolean esEdicion = etiquetaOriginal != null && !etiquetaOriginal.isBlank();
        Tortuga destino;

        if (esEdicion) {
            if (!etiquetaOriginal.equals(tortuga.getEtiquetaTortuga())) {
                throw new IllegalArgumentException("La etiqueta de la tortuga no se puede modificar.");
            }
            destino = tortugaRepository.findById(etiquetaOriginal)
                    .orElseThrow(() -> new IllegalArgumentException("La tortuga que se intenta actualizar no existe."));
        } else {
            if (tortugaRepository.existsById(tortuga.getEtiquetaTortuga())) {
                throw new IllegalArgumentException("Ya existe una tortuga con esa etiqueta.");
            }
            destino = new Tortuga();
            destino.setEtiquetaTortuga(tortuga.getEtiquetaTortuga());
        }

        destino.setEspecie(tortuga.getEspecie());
        destino.setSexo(tortuga.getSexo());
        destino.setFechaRegistro(tortuga.getFechaRegistro());
        destino.setObservaciones(tortuga.getObservaciones());
        destino.setEstado(estadoService.obtenerEstado(idEstado(tortuga)));

        return tortugaRepository.save(destino);
    }

    private Integer idEstado(Tortuga tortuga) {
        if (tortuga.getEstado() == null || tortuga.getEstado().getIdEstado() == null) {
            throw new IllegalArgumentException("Debe seleccionar un estado válido.");
        }
        return tortuga.getEstado().getIdEstado();
    }

    @Transactional
    public void delete(String etiquetaTortuga){

        if(!tortugaRepository.existsById(etiquetaTortuga)){
            throw new IllegalArgumentException("La tortuga no existe.");
        }

        try{
            tortugaRepository.deleteById(etiquetaTortuga);
        }catch(DataIntegrityViolationException e){
            throw new IllegalStateException("No se puede eliminar la tortuga porque tiene datos asociados.",e);
        }

    }

}
