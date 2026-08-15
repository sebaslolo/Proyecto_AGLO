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
    private final HerramientaService herramientaService;
    private final UsuarioService usuarioService;
    private final EstadoService estadoService;

    public PrestamoService(PrestamoRepository prestamoRepository,
            HerramientaService herramientaService,
            UsuarioService usuarioService,
            EstadoService estadoService){
        this.prestamoRepository=prestamoRepository;
        this.herramientaService = herramientaService;
        this.usuarioService = usuarioService;
        this.estadoService = estadoService;
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
        Prestamo destino = prestamo.getIdPrestamo() == null
                ? new Prestamo()
                : prestamoRepository.findById(prestamo.getIdPrestamo())
                        .orElseThrow(() -> new IllegalArgumentException("El préstamo que se intenta actualizar no existe."));

        destino.setHerramienta(herramientaService.getHerramienta(idHerramienta(prestamo))
                .orElseThrow(() -> new IllegalArgumentException("La herramienta seleccionada no existe.")));
        destino.setUsuario(usuarioService.obtenerUsuarioAsignableAPrestamo(idUsuario(prestamo)));
        destino.setEstado(estadoService.obtenerEstado(idEstado(prestamo)));
        destino.setFechaPrestamo(prestamo.getFechaPrestamo());
        destino.setFechaDevolucion(prestamo.getFechaDevolucion());

        return prestamoRepository.save(destino);
    }

    private Integer idHerramienta(Prestamo prestamo) {
        if (prestamo.getHerramienta() == null || prestamo.getHerramienta().getIdHerramienta() == null) {
            throw new IllegalArgumentException("Debe seleccionar una herramienta válida.");
        }
        return prestamo.getHerramienta().getIdHerramienta();
    }

    private Integer idUsuario(Prestamo prestamo) {
        if (prestamo.getUsuario() == null || prestamo.getUsuario().getIdUsuario() == null) {
            throw new IllegalArgumentException("Debe seleccionar un usuario válido.");
        }
        return prestamo.getUsuario().getIdUsuario();
    }

    private Integer idEstado(Prestamo prestamo) {
        if (prestamo.getEstado() == null || prestamo.getEstado().getIdEstado() == null) {
            throw new IllegalArgumentException("Debe seleccionar un estado válido.");
        }
        return prestamo.getEstado().getIdEstado();
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
