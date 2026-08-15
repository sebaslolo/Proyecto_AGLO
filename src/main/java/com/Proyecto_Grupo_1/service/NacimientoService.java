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
    private final NidoService nidoService;
    private final EstadoService estadoService;


    public NacimientoService(NacimientoRepository nacimientoRepository,
            NidoService nidoService,
            EstadoService estadoService){

        this.nacimientoRepository = nacimientoRepository;
        this.nidoService = nidoService;
        this.estadoService = estadoService;

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

        Nacimiento destino = nacimiento.getIdNacimiento() == null
                ? new Nacimiento()
                : nacimientoRepository.findById(nacimiento.getIdNacimiento())
                        .orElseThrow(() -> new IllegalArgumentException("El nacimiento que se intenta actualizar no existe."));

        destino.setNido(nidoService.getNido(idNido(nacimiento))
                .orElseThrow(() -> new IllegalArgumentException("El nido seleccionado no existe.")));
        destino.setFechaEclosion(nacimiento.getFechaEclosion());
        destino.setCriasVivas(nacimiento.getCriasVivas());
        destino.setCriasMuertas(nacimiento.getCriasMuertas());
        destino.setCriasInfertiles(nacimiento.getCriasInfertiles());
        destino.setObservaciones(nacimiento.getObservaciones());
        destino.setEstado(estadoService.obtenerEstado(idEstado(nacimiento)));

        return nacimientoRepository.save(destino);

    }

    private Integer idNido(Nacimiento nacimiento) {
        if (nacimiento.getNido() == null || nacimiento.getNido().getIdNido() == null) {
            throw new IllegalArgumentException("Debe seleccionar un nido válido.");
        }
        return nacimiento.getNido().getIdNido();
    }

    private Integer idEstado(Nacimiento nacimiento) {
        if (nacimiento.getEstado() == null || nacimiento.getEstado().getIdEstado() == null) {
            throw new IllegalArgumentException("Debe seleccionar un estado válido.");
        }
        return nacimiento.getEstado().getIdEstado();
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
