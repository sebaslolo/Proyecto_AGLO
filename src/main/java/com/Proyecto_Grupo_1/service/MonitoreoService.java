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

    public MonitoreoService(MonitoreoRepository monitoreoRepository) {
        this.monitoreoRepository = monitoreoRepository;
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
        return monitoreoRepository.save(monitoreo);
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