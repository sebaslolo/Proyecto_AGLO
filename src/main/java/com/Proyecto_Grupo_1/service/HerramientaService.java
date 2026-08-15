package com.Proyecto_Grupo_1.service;

import com.Proyecto_Grupo_1.domain.Herramienta;
import com.Proyecto_Grupo_1.repository.HerramientaRepository;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
public class HerramientaService {

    private final HerramientaRepository herramientaRepository;
    private final EstadoService estadoService;

    public HerramientaService(HerramientaRepository herramientaRepository,
            EstadoService estadoService) {
        this.herramientaRepository = herramientaRepository;
        this.estadoService = estadoService;
    }

    @Transactional(readOnly = true)
    public List<Herramienta> getHerramientas(boolean sinFiltro){
        return herramientaRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Herramienta> getHerramienta(Integer idHerramienta){
        return herramientaRepository.findById(idHerramienta);
    }

    @Transactional
    public Herramienta save(@Valid Herramienta herramienta){
        Herramienta destino = herramienta.getIdHerramienta() == null
                ? new Herramienta()
                : herramientaRepository.findById(herramienta.getIdHerramienta())
                        .orElseThrow(() -> new IllegalArgumentException("La herramienta que se intenta actualizar no existe."));

        destino.setNombreHerramienta(herramienta.getNombreHerramienta());
        destino.setDescripcion(herramienta.getDescripcion());
        destino.setEstado(estadoService.obtenerEstado(idEstado(herramienta)));

        return herramientaRepository.save(destino);
    }

    private Integer idEstado(Herramienta herramienta) {
        if (herramienta.getEstado() == null || herramienta.getEstado().getIdEstado() == null) {
            throw new IllegalArgumentException("Debe seleccionar un estado válido.");
        }
        return herramienta.getEstado().getIdEstado();
    }

    @Transactional
    public void delete(Integer idHerramienta){

        if(!herramientaRepository.existsById(idHerramienta)){
            throw new IllegalArgumentException("La herramienta no existe.");
        }

        try{
            herramientaRepository.deleteById(idHerramienta);
        }catch(DataIntegrityViolationException e){
            throw new IllegalStateException("No se puede eliminar la herramienta porque tiene datos asociados.",e);
        }

    }

}
