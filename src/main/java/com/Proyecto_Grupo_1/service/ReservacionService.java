package com.Proyecto_Grupo_1.service;

import com.Proyecto_Grupo_1.domain.Actividad;
import com.Proyecto_Grupo_1.domain.ActividadDetalle;
import com.Proyecto_Grupo_1.domain.ActividadDetalleId;
import com.Proyecto_Grupo_1.domain.Estado;
import com.Proyecto_Grupo_1.domain.Reservacion;
import com.Proyecto_Grupo_1.domain.Usuario;
import com.Proyecto_Grupo_1.repository.ActividadDetalleRepository;
import com.Proyecto_Grupo_1.repository.ReservacionRepository;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
public class ReservacionService {

    private final ReservacionRepository reservacionRepository;
    private final ActividadDetalleRepository actividadDetalleRepository;
    private final ActividadService actividadService;
    private final UsuarioService usuarioService;
    private final EstadoService estadoService;

    public ReservacionService(ReservacionRepository reservacionRepository,
            ActividadDetalleRepository actividadDetalleRepository,
            ActividadService actividadService,
            UsuarioService usuarioService,
            EstadoService estadoService) {
        this.reservacionRepository = reservacionRepository;
        this.actividadDetalleRepository = actividadDetalleRepository;
        this.actividadService = actividadService;
        this.usuarioService = usuarioService;
        this.estadoService = estadoService;
    }

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

    @Transactional(readOnly = true)
    public long obtenerPersonasReservadas(Integer idActividad) {
        Long total = actividadDetalleRepository.sumarPersonasPorActividad(idActividad);
        return total == null ? 0L : total;
    }

    @Transactional(readOnly = true)
    public long obtenerCupoDisponible(Integer idActividad) {
        Actividad actividad = actividadService.obtenerActividad(idActividad);
        long cupoMaximo = actividad.getCupoMaximo() == null ? 0L : actividad.getCupoMaximo();
        return Math.max(0L, cupoMaximo - obtenerPersonasReservadas(idActividad));
    }

    @Transactional
    public Reservacion save(@Valid Reservacion reservacion) {
        return reservacionRepository.save(reservacion);
    }

    @Transactional
    public Reservacion crearReservacion(Integer idUsuario, Integer idActividad, Integer cantidadPersonas) {
        if (cantidadPersonas == null || cantidadPersonas < 1) {
            throw new IllegalArgumentException("La cantidad de personas debe ser mayor a cero.");
        }

        Actividad actividad = actividadService.obtenerActividad(idActividad);
        Usuario usuario = usuarioService.obtenerUsuario(idUsuario);
        Estado estadoPendiente = estadoService.obtenerEstadoPorNombre("Pendiente");
        long cupoDisponible = obtenerCupoDisponible(idActividad);
        if (cantidadPersonas > cupoDisponible) {
            throw new IllegalStateException("No hay cupos suficientes para confirmar la reservación.");
        }

        BigDecimal precioUnitario = actividad.getPrecioActual();
        BigDecimal subtotal = precioUnitario.multiply(BigDecimal.valueOf(cantidadPersonas));

        Reservacion reservacion = new Reservacion();
        reservacion.setUsuario(usuario);
        reservacion.setEstado(estadoPendiente);
        reservacion.setFechaReservacion(LocalDateTime.now());
        reservacion.setMontoTotal(subtotal);
        Reservacion guardada = reservacionRepository.save(reservacion);

        ActividadDetalle detalle = new ActividadDetalle();
        detalle.setId(new ActividadDetalleId(guardada.getIdReservacion(), actividad.getIdActividad()));
        detalle.setReservacion(guardada);
        detalle.setActividad(actividad);
        detalle.setCantidadPersonas(cantidadPersonas);
        detalle.setPrecioUnitario(precioUnitario);
        detalle.setSubtotal(subtotal);
        detalle.setEstado(estadoPendiente);
        actividadDetalleRepository.save(detalle);

        return guardada;
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
            actividadDetalleRepository.deleteAll(actividadDetalleRepository.findByReservacionIdReservacion(idReservacion));
            reservacionRepository.deleteById(idReservacion);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException("No se puede eliminar la reservacion. Tiene datos asociados.", e);
        }
    }
}
