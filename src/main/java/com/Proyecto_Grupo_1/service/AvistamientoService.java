package com.Proyecto_Grupo_1.service;

import com.Proyecto_Grupo_1.domain.Avistamiento;
import com.Proyecto_Grupo_1.repository.AvistamientoRepository;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
public class AvistamientoService {

    private final AvistamientoRepository avistamientoRepository;
    private final TortugaService tortugaService;
    private final EstadoService estadoService;

    public AvistamientoService(AvistamientoRepository avistamientoRepository,
            TortugaService tortugaService,
            EstadoService estadoService){
        this.avistamientoRepository=avistamientoRepository;
        this.tortugaService = tortugaService;
        this.estadoService = estadoService;
    }

    @Transactional(readOnly = true)
    public List<Avistamiento> getAvistamientos(boolean sinFiltro){
        return avistamientoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Avistamiento> getAvistamiento(Integer idAvistamiento){
        return avistamientoRepository.findById(idAvistamiento);
    }

    @Transactional
    public Avistamiento save(@Valid Avistamiento avistamiento){
        Avistamiento destino = avistamiento.getIdAvistamiento() == null
                ? new Avistamiento()
                : avistamientoRepository.findById(avistamiento.getIdAvistamiento())
                        .orElseThrow(() -> new IllegalArgumentException("El avistamiento que se intenta actualizar no existe."));

        destino.setTortuga(tortugaService.getTortuga(etiquetaTortuga(avistamiento))
                .orElseThrow(() -> new IllegalArgumentException("La tortuga seleccionada no existe.")));
        destino.setComportamiento(avistamiento.getComportamiento());
        destino.setUbicacion(avistamiento.getUbicacion());
        destino.setFechaAvistamiento(avistamiento.getFechaAvistamiento());
        destino.setObservaciones(avistamiento.getObservaciones());
        destino.setEstado(estadoService.obtenerEstado(idEstado(avistamiento)));

        return avistamientoRepository.save(destino);
    }

    private String etiquetaTortuga(Avistamiento avistamiento) {
        if (avistamiento.getTortuga() == null
                || avistamiento.getTortuga().getEtiquetaTortuga() == null
                || avistamiento.getTortuga().getEtiquetaTortuga().isBlank()) {
            throw new IllegalArgumentException("Debe seleccionar una tortuga válida.");
        }
        return avistamiento.getTortuga().getEtiquetaTortuga();
    }

    private Integer idEstado(Avistamiento avistamiento) {
        if (avistamiento.getEstado() == null || avistamiento.getEstado().getIdEstado() == null) {
            throw new IllegalArgumentException("Debe seleccionar un estado válido.");
        }
        return avistamiento.getEstado().getIdEstado();
    }

    @Transactional
    public void delete(Integer idAvistamiento){

        if(!avistamientoRepository.existsById(idAvistamiento)){
            throw new IllegalArgumentException("El avistamiento no existe.");
        }

        try{
            avistamientoRepository.deleteById(idAvistamiento);
        }catch(DataIntegrityViolationException e){
            throw new IllegalStateException("No se puede eliminar el avistamiento.",e);
        }

    }

}
