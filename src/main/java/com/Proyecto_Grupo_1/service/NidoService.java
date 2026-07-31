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

    public NidoService(NidoRepository nidoRepository) {
        this.nidoRepository = nidoRepository;
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
        return nidoRepository.save(nido);
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