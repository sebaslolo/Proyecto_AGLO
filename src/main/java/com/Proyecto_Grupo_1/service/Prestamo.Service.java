package com.Proyecto_Grupo_1.service;

import com.Proyecto_Grupo_1.domain.Prestamo;
import com.Proyecto_Grupo_1.repository.PrestamoRepository;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
public class PrestamoService {

    private final PrestamoRepository prestamoRepository;

    public PrestamoService(PrestamoRepository prestamoRepository){
        this.prestamoRepository=prestamoRepository;
    }

    @Transactional(readOnly = true)
    public List<Prestamo> getPrestamos(boolean sinFiltro){
        return prestamoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Prestamo> getPrestamo(Integer idPrestamo){
        return prestamoRepository.findById(idPrestamo);
    }

    @Transactional
    public Prestamo save(@Valid Prestamo prestamo){
        return prestamoRepository.save(prestamo);
    }

    @Transactional
    public void delete(Integer idPrestamo){

        if(!prestamoRepository.existsById(idPrestamo)){
            throw new IllegalArgumentException("El préstamo no existe.");
        }

        try{
            prestamoRepository.deleteById(idPrestamo);
        }catch(DataIntegrityViolationException e){
            throw new IllegalStateException("No se puede eliminar el préstamo.",e);
        }

    }

}