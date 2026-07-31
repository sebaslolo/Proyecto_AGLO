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

    public AvistamientoService(AvistamientoRepository avistamientoRepository){
        this.avistamientoRepository=avistamientoRepository;
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
        return avistamientoRepository.save(avistamiento);
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