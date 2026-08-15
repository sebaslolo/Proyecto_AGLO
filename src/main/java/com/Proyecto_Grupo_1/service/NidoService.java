package com.Proyecto_Grupo_1.service;

import com.Proyecto_Grupo_1.domain.Nido;
import com.Proyecto_Grupo_1.repository.NidoRepository;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
public class NidoService {

    private final NidoRepository nidoRepository;
    private final TortugaService tortugaService;
    private final EstadoService estadoService;

    public NidoService(NidoRepository nidoRepository,
            TortugaService tortugaService,
            EstadoService estadoService) {
        this.nidoRepository = nidoRepository;
        this.tortugaService = tortugaService;
        this.estadoService = estadoService;
    }

    @Transactional(readOnly = true)
    public List<Nido> getNidos(boolean sinFiltro){
        return nidoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Nido> getNido(Integer idNido){
        return nidoRepository.findById(idNido);
    }

    @Transactional
    public Nido save(@Valid Nido nido){
        Nido destino = nido.getIdNido() == null
                ? new Nido()
                : nidoRepository.findById(nido.getIdNido())
                        .orElseThrow(() -> new IllegalArgumentException("El nido que se intenta actualizar no existe."));

        destino.setTortuga(tortugaService.getTortuga(etiquetaTortuga(nido))
                .orElseThrow(() -> new IllegalArgumentException("La tortuga seleccionada no existe.")));
        destino.setUbicacion(nido.getUbicacion());
        destino.setFechaAnidacion(nido.getFechaAnidacion());
        destino.setCantidadHuevos(nido.getCantidadHuevos());
        destino.setProfundidadNido(nido.getProfundidadNido());
        destino.setObservaciones(nido.getObservaciones());
        destino.setEstado(estadoService.obtenerEstado(idEstado(nido)));

        return nidoRepository.save(destino);
    }

    private String etiquetaTortuga(Nido nido) {
        if (nido.getTortuga() == null
                || nido.getTortuga().getEtiquetaTortuga() == null
                || nido.getTortuga().getEtiquetaTortuga().isBlank()) {
            throw new IllegalArgumentException("Debe seleccionar una tortuga válida.");
        }
        return nido.getTortuga().getEtiquetaTortuga();
    }

    private Integer idEstado(Nido nido) {
        if (nido.getEstado() == null || nido.getEstado().getIdEstado() == null) {
            throw new IllegalArgumentException("Debe seleccionar un estado válido.");
        }
        return nido.getEstado().getIdEstado();
    }

    @Transactional
    public void delete(Integer idNido){

        if(!nidoRepository.existsById(idNido)){
            throw new IllegalArgumentException("El nido no existe.");
        }

        try{

            nidoRepository.deleteById(idNido);

        }catch(DataIntegrityViolationException e){

            throw new IllegalStateException(
                    "No se puede eliminar el nido porque tiene datos asociados.",
                    e
            );

        }

    }

}
