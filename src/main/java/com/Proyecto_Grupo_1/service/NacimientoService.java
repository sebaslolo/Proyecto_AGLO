package com.Proyecto_Grupo_1.service;

import com.Proyecto_Grupo_1.domain.Nacimiento;
import com.Proyecto_Grupo_1.repository.NacimientoRepository;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
public class NacimientoService {

    private final NacimientoRepository nacimientoRepository;


    public NacimientoService(NacimientoRepository nacimientoRepository){

        this.nacimientoRepository = nacimientoRepository;

    }


    @Transactional(readOnly = true)
    public List<Nacimiento> getNacimientos(boolean sinFiltro){

        return nacimientoRepository.findAll();

    }


    @Transactional(readOnly = true)
    public Optional<Nacimiento> getNacimiento(Integer idNacimiento){

        return nacimientoRepository.findById(idNacimiento);

    }


    @Transactional
    public Nacimiento save(@Valid Nacimiento nacimiento){

        return nacimientoRepository.save(nacimiento);

    }


    @Transactional
    public void delete(Integer idNacimiento){


        if(!nacimientoRepository.existsById(idNacimiento)){

            throw new IllegalArgumentException(
                    "El nacimiento no existe."
            );

        }


        try{

            nacimientoRepository.deleteById(idNacimiento);


        }catch(DataIntegrityViolationException e){

            throw new IllegalStateException(
                    "No se puede eliminar el nacimiento.",
                    e
            );

        }

    }

}