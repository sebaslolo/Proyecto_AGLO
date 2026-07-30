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

    public HerramientaService(HerramientaRepository herramientaRepository) {
        this.herramientaRepository = herramientaRepository;
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
        return herramientaRepository.save(herramienta);
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