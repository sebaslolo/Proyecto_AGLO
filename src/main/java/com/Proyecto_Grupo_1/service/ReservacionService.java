package com.Proyecto_Grupo_1.service;

import com.Proyecto_Grupo_1.domain.Actividad;
import com.Proyecto_Grupo_1.domain.ActividadDetalle;
import com.Proyecto_Grupo_1.domain.ActividadDetalleId;
import com.Proyecto_Grupo_1.domain.Estado;
import com.Proyecto_Grupo_1.domain.Reservacion;
import com.Proyecto_Grupo_1.domain.Usuario;
import com.Proyecto_Grupo_1.repository.ActividadDetalleRepository;
import com.Proyecto_Grupo_1.repository.ReservacionRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import jakarta.mail.MessagingException;

@Service

@Validated
public class ReservacionService {

    private final ReservacionRepository reservacionRepository;
    private final ActividadDetalleRepository actividadDetalleRepository;
    private final ActividadService actividadService;
    private final UsuarioService usuarioService;
    private final EstadoService estadoService;
    private final CorreoService correoService;

    public ReservacionService(ReservacionRepository reservacionRepository,
            ActividadDetalleRepository actividadDetalleRepository,
            ActividadService actividadService,
            UsuarioService usuarioService,
            EstadoService estadoService,
            CorreoService correoService) {

        this.reservacionRepository = reservacionRepository;
        this.actividadDetalleRepository = actividadDetalleRepository;
        this.actividadService = actividadService;
        this.usuarioService = usuarioService;
        this.estadoService = estadoService;
        this.correoService = correoService;
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
    public Reservacion crearReservacion(Integer idUsuario, Integer idActividad, Integer cantidadPersonas) {
        return crearReservacionConUsuario(
                usuarioService.obtenerUsuario(validarIdUsuario(idUsuario)),
                idActividad,
                cantidadPersonas);
    }

    /**
     * Alta administrativa segura: el usuario enviado por el formulario debe
     * seguir siendo un CLIENTE activo cuando se procesa la solicitud.
     */
    @Transactional
    public Reservacion crearReservacionAdministrativa(
            Integer idUsuario,
            Integer idActividad,
            Integer cantidadPersonas) {
        return crearReservacionConUsuario(
                usuarioService.obtenerClienteActivoParaReservacion(idUsuario),
                idActividad,
                cantidadPersonas);
    }

    private Reservacion crearReservacionConUsuario(
            Usuario usuario,
            Integer idActividad,
            Integer cantidadPersonas) {

        if (cantidadPersonas == null || cantidadPersonas < 1) {
            throw new IllegalArgumentException("La cantidad de personas debe ser mayor a cero.");
        }

        if (idActividad == null) {
            throw new IllegalArgumentException("Debe seleccionar una actividad válida.");
        }

        Actividad actividad = actividadService.obtenerActividad(idActividad);
        Estado estadoPendiente = estadoService.obtenerEstadoPorNombre("Pendiente");

        long cupoDisponible = obtenerCupoDisponible(idActividad);

        if (cantidadPersonas > cupoDisponible) {
            throw new IllegalStateException("No hay cupos suficientes para confirmar la reservación.");
        }

        BigDecimal precioUnitario = actividad.getPrecioActual();

        if (precioUnitario == null) {
            throw new IllegalStateException("La actividad seleccionada no tiene un precio válido.");
        }

        BigDecimal subtotal = precioUnitario.multiply(
                BigDecimal.valueOf(cantidadPersonas));

        Reservacion reservacion = new Reservacion();
        reservacion.setUsuario(usuario);
        reservacion.setEstado(estadoPendiente);
        reservacion.setFechaReservacion(LocalDateTime.now());
        reservacion.setMontoTotal(subtotal);

        Reservacion guardada = reservacionRepository.save(reservacion);

        ActividadDetalle detalle = new ActividadDetalle();
        detalle.setId(new ActividadDetalleId(
                guardada.getIdReservacion(),
                actividad.getIdActividad()));

        detalle.setReservacion(guardada);
        detalle.setActividad(actividad);
        detalle.setCantidadPersonas(cantidadPersonas);
        detalle.setPrecioUnitario(precioUnitario);
        detalle.setSubtotal(subtotal);
        detalle.setEstado(estadoPendiente);

        actividadDetalleRepository.save(detalle);

        enviarCorreoConfirmacion(
                usuario,
                guardada,
                actividad,
                cantidadPersonas,
                precioUnitario,
                subtotal,
                estadoPendiente);

        return guardada;
    }

    private void enviarCorreoConfirmacion(
            Usuario usuario,
            Reservacion reservacion,
            Actividad actividad,
            Integer cantidadPersonas,
            BigDecimal precioUnitario,
            BigDecimal subtotal,
            Estado estado) {

        try {

            if (usuario.getCorreo() == null || usuario.getCorreo().isBlank()) {
                return;
            }

            String asunto = "Confirmación de Reservación #"
                    + reservacion.getIdReservacion()
                    + " - AGLO";

            String contenido
                    = "<html>"
                    + "<body style='font-family:Arial,sans-serif;background:#f4f4f4;padding:30px;'>"
                    + "<div style='max-width:700px;margin:auto;background:white;border-radius:10px;overflow:hidden;'>"
                    + "<div style='background:#198754;padding:20px;text-align:center;'>"
                    + "<h1 style='color:white;margin:0;'>AGLO</h1>"
                    + "<p style='color:white;margin-top:8px;'>Confirmación de Reservación</p>"
                    + "</div>"
                    + "<div style='padding:30px;'>"
                    + "<h2 style='color:#198754;'>¡Hola "
                    + usuario.getNombre()
                    + "!</h2>"
                    + "<p>Su reservación fue registrada exitosamente.</p>"
                    + "<table style='width:100%;border-collapse:collapse;'>"
                    + "<tr>"
                    + "<td style='padding:10px;border:1px solid #ddd;'><b>Reservación</b></td>"
                    + "<td style='padding:10px;border:1px solid #ddd;'>#"
                    + reservacion.getIdReservacion()
                    + "</td>"
                    + "</tr>"
                    + "<tr>"
                    + "<td style='padding:10px;border:1px solid #ddd;'><b>Actividad</b></td>"
                    + "<td style='padding:10px;border:1px solid #ddd;'>"
                    + actividad.getNombreActividad()
                    + "</td>"
                    + "</tr>"
                    + "<tr>"
                    + "<td style='padding:10px;border:1px solid #ddd;'><b>Cantidad de personas</b></td>"
                    + "<td style='padding:10px;border:1px solid #ddd;'>"
                    + cantidadPersonas
                    + "</td>"
                    + "</tr>"
                    + "<tr>"
                    + "<td style='padding:10px;border:1px solid #ddd;'><b>Precio por persona</b></td>"
                    + "<td style='padding:10px;border:1px solid #ddd;'>₡"
                    + precioUnitario
                    + "</td>"
                    + "</tr>"
                    + "<tr>"
                    + "<td style='padding:10px;border:1px solid #ddd;'><b>Total</b></td>"
                    + "<td style='padding:10px;border:1px solid #ddd;'>₡"
                    + subtotal
                    + "</td>"
                    + "</tr>"
                    + "<tr>"
                    + "<td style='padding:10px;border:1px solid #ddd;'><b>Estado</b></td>"
                    + "<td style='padding:10px;border:1px solid #ddd;'>"
                    + estado.getNombreEstado()
                    + "</td>"
                    + "</tr>"
                    + "<tr>"
                    + "<td style='padding:10px;border:1px solid #ddd;'><b>Fecha de Reservación</b></td>"
                    + "<td style='padding:10px;border:1px solid #ddd;'>"
                    + reservacion.getFechaReservacion()
                    + "</td>"
                    + "</tr>"
                    + "</table>"
                    + "<br>"
                    + "<p>Gracias por confiar en <strong>AGLO</strong>.</p>"
                    + "<p>Este correo sirve como comprobante de su reservación.</p>"
                    + "</div>"
                    + "<div style='background:#198754;color:white;padding:15px;text-align:center;'>"
                    + "AGLO © 2026"
                    + "</div>"
                    + "</div>"
                    + "</body>"
                    + "</html>";

            correoService.enviarCorreoHtml(
                    usuario.getCorreo(),
                    asunto,
                    contenido);

        } catch (MessagingException e) {

            System.err.println(
                    "No se pudo enviar el correo de confirmación: "
                    + e.getMessage());
        }
    }

    @Transactional
    public Reservacion actualizarEstadoReservacion(Integer idReservacion, Integer idEstado) {
        if (idReservacion == null) {
            throw new IllegalArgumentException("La reservación seleccionada no existe.");
        }
        if (idEstado == null) {
            throw new IllegalArgumentException("Debe seleccionar un estado válido.");
        }

        Reservacion reservacion = reservacionRepository.findById(idReservacion)
                .orElseThrow(() -> new IllegalArgumentException("La reservación seleccionada no existe."));
        Estado estado = estadoService.obtenerEstado(idEstado);

        reservacion.setEstado(estado);
        List<ActividadDetalle> detalles = actividadDetalleRepository.findByReservacionIdReservacion(idReservacion);
        detalles.forEach(detalle -> detalle.setEstado(estado));
        actividadDetalleRepository.saveAll(detalles);

        return reservacionRepository.save(reservacion);
    }

    private Integer validarIdUsuario(Integer idUsuario) {
        if (idUsuario == null) {
            throw new IllegalArgumentException("No se encontró el usuario de la reservación.");
        }
        return idUsuario;
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
