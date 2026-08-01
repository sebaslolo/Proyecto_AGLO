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

    public TortugaService(TortugaRepository tortugaRepository) {
        this.tortugaRepository = tortugaRepository;
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
        return tortugaRepository.save(tortuga);
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