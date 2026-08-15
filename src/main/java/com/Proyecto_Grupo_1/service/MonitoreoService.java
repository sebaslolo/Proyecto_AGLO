package com.Proyecto_Grupo_1.service;

import com.Proyecto_Grupo_1.domain.Monitoreo;
import com.Proyecto_Grupo_1.repository.MonitoreoRepository;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
public class MonitoreoService {

    private final MonitoreoRepository monitoreoRepository;
    private final GuiaService guiaService;
    private final EstadoService estadoService;

    public MonitoreoService(MonitoreoRepository monitoreoRepository,
            GuiaService guiaService,
            EstadoService estadoService) {
        this.monitoreoRepository = monitoreoRepository;
        this.guiaService = guiaService;
        this.estadoService = estadoService;
    }

    @Transactional(readOnly = true)
    public List<Monitoreo> getMonitoreos(boolean sinFiltro) {
        return monitoreoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Monitoreo> getMonitoreo(Integer idMonitoreo) {
        return monitoreoRepository.findById(idMonitoreo);
    }

    @Transactional
    public Monitoreo save(@Valid Monitoreo monitoreo) {
        Monitoreo destino = monitoreo.getIdMonitoreo() == null
                ? new Monitoreo()
                : monitoreoRepository.findById(monitoreo.getIdMonitoreo())
                        .orElseThrow(() -> new IllegalArgumentException("El monitoreo que se intenta actualizar no existe."));

        destino.setGuia(guiaService.obtenerGuia(idGuia(monitoreo)));
        destino.setFechaMonitoreo(monitoreo.getFechaMonitoreo());
        destino.setEstado(estadoService.obtenerEstado(idEstado(monitoreo)));

        return monitoreoRepository.save(destino);
    }

    private Integer idGuia(Monitoreo monitoreo) {
        if (monitoreo.getGuia() == null || monitoreo.getGuia().getIdGuia() == null) {
            throw new IllegalArgumentException("Debe seleccionar un guía válido.");
        }
        return monitoreo.getGuia().getIdGuia();
    }

    private Integer idEstado(Monitoreo monitoreo) {
        if (monitoreo.getEstado() == null || monitoreo.getEstado().getIdEstado() == null) {
            throw new IllegalArgumentException("Debe seleccionar un estado válido.");
        }
        return monitoreo.getEstado().getIdEstado();
    }

    @Transactional
    public void delete(Integer idMonitoreo) {

        if (!monitoreoRepository.existsById(idMonitoreo)) {
            throw new IllegalArgumentException("El monitoreo no existe.");
        }

        try {
            monitoreoRepository.deleteById(idMonitoreo);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException(
                    "No se puede eliminar el monitoreo porque tiene datos asociados.",
                    e);
        }

    }

}
