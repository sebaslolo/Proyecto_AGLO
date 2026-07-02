package com.Proyecto_Grupo_1.service;

import com.Proyecto_Grupo_1.domain.ActividadDetalle;
import com.Proyecto_Grupo_1.domain.Reservacion;
import com.Proyecto_Grupo_1.repository.ActividadDetalleRepository;
import com.Proyecto_Grupo_1.repository.ReservacionRepository;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
@RequiredArgsConstructor
public class ReservacionService {

    private final ReservacionRepository reservacionRepository;
    private final ActividadDetalleRepository actividadDetalleRepository;

    @Transactional(readOnly = true)
    public List<Reservacion> getReservaciones(boolean sinFiltro) {
        return reservacionRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Reservacion> getReservacion(Integer idReservacion) {
        return reservacionRepository.findById(idReservacion);
    }

    @Transactional(readOnly = true)
    public List<Reservacion> listarReservaciones() {
        return getReservaciones(false);
    }

    @Transactional(readOnly = true)
    public List<Reservacion> listarReservacionesPorUsuario(Integer idUsuario) {
        return reservacionRepository.findByUsuarioIdUsuarioOrderByFechaReservacionDesc(idUsuario);
    }

    @Transactional(readOnly = true)
    public Reservacion obtenerReservacion(Integer idReservacion) {
        return reservacionRepository.findById(idReservacion)
                .orElseThrow(() -> new IllegalArgumentException("Reservacion no encontrada: " + idReservacion));
    }

    @Transactional(readOnly = true)
    public List<ActividadDetalle> obtenerDetalle(Integer idReservacion) {
        return actividadDetalleRepository.findByReservacionIdReservacion(idReservacion);
    }

    @Transactional
    public Reservacion save(@Valid Reservacion reservacion) {
        return reservacionRepository.save(reservacion);
    }

    @Transactional
    public Reservacion guardarReservacion(@Valid Reservacion reservacion) {
        return save(reservacion);
    }

    @Transactional
    public void delete(Integer idReservacion) {
        if (!reservacionRepository.existsById(idReservacion)) {
            throw new IllegalArgumentException("La reservacion con ID " + idReservacion + " no existe.");
        }
        try {
            reservacionRepository.deleteById(idReservacion);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException("No se puede eliminar la reservacion. Tiene datos asociados.", e);
        }
    }
}
