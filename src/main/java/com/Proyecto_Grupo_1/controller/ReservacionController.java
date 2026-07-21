package com.Proyecto_Grupo_1.controller;

import com.Proyecto_Grupo_1.domain.Reservacion;
import com.Proyecto_Grupo_1.dto.ReservacionForm;
import com.Proyecto_Grupo_1.service.ActividadService;
import com.Proyecto_Grupo_1.service.ReservacionService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import java.util.Collection;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ReservacionController {

    private final ReservacionService reservacionService;
    private final ActividadService actividadService;
    private final MessageSource messageSource;

    public ReservacionController(ReservacionService reservacionService,
            ActividadService actividadService,
            MessageSource messageSource) {
        this.reservacionService = reservacionService;
        this.actividadService = actividadService;
        this.messageSource = messageSource;
    }

    @GetMapping("/admin/reservaciones")
    public String indexAdmin() {
        return "redirect:/admin/reservaciones/listado";
    }

    @GetMapping("/admin/reservaciones/listado")
    public String listadoAdmin(Model model) {
        var reservaciones = reservacionService.getReservaciones(false);
        model.addAttribute("reservaciones", reservaciones);
        model.addAttribute("totalReservaciones", reservaciones.size());
        return "/admin/reservaciones/listado";
    }

    @GetMapping("/admin/reservaciones/detalle/{idReservacion}")
    public String detalleAdmin(@PathVariable Integer idReservacion, Model model, RedirectAttributes redirectAttributes) {
        var reservacionOpt = reservacionService.getReservacion(idReservacion);
        if (reservacionOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", msg("reservacion.error.noExiste"));
            return "redirect:/admin/reservaciones/listado";
        }
        model.addAttribute("reservacion", reservacionOpt.get());
        model.addAttribute("detalles", reservacionService.obtenerDetalle(idReservacion));
        return "/admin/reservaciones/detalle";
    }

    @GetMapping("/reservaciones/nueva")
    public String nueva(@RequestParam Integer idActividad, Model model, RedirectAttributes redirectAttributes) {
        var actividadOpt = actividadService.getActividad(idActividad);
        if (actividadOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", msg("actividad.error.noExiste"));
            return "redirect:/catalogo/listado";
        }
        ReservacionForm reservacionForm = new ReservacionForm();
        reservacionForm.setIdActividad(idActividad);
        model.addAttribute("reservacionForm", reservacionForm);
        model.addAttribute("actividad", actividadOpt.get());
        model.addAttribute("cupoDisponible", reservacionService.obtenerCupoDisponible(idActividad));
        return "/reservaciones/modifica";
    }

    @PostMapping("/reservaciones/guardar")
    public String guardar(@Valid ReservacionForm reservacionForm,
            BindingResult bindingResult,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (reservacionForm.getIdActividad() == null) {
            redirectAttributes.addFlashAttribute("error", msg("actividad.error.noExiste"));
            return "redirect:/catalogo/listado";
        }
        var actividadOpt = actividadService.getActividad(reservacionForm.getIdActividad());
        if (actividadOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", msg("actividad.error.noExiste"));
            return "redirect:/catalogo/listado";
        }
        if (bindingResult.hasErrors()) {
            model.addAttribute("actividad", actividadOpt.get());
            model.addAttribute("cupoDisponible", reservacionService.obtenerCupoDisponible(reservacionForm.getIdActividad()));
            return "/reservaciones/modifica";
        }
        Integer idUsuario = (Integer) session.getAttribute("idUsuario");
        try {
            var guardada = reservacionService.crearReservacion(
                    idUsuario,
                    reservacionForm.getIdActividad(),
                    reservacionForm.getCantidadPersonas());
            redirectAttributes.addFlashAttribute("todoOk", msg("reservacion.mensaje.guardada"));
            return "redirect:/reservaciones/confirmacion/" + guardada.getIdReservacion();
        } catch (IllegalArgumentException | IllegalStateException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("actividad", actividadOpt.get());
            model.addAttribute("cupoDisponible", reservacionService.obtenerCupoDisponible(reservacionForm.getIdActividad()));
            return "/reservaciones/modifica";
        }
    }

    @PostMapping("/admin/reservaciones/eliminar")
    public String eliminar(@RequestParam Integer idReservacion, RedirectAttributes redirectAttributes) {
        String titulo = "todoOk";
        String detalle = msg("reservacion.mensaje.eliminada");
        try {
            reservacionService.delete(idReservacion);
        } catch (IllegalArgumentException e) {
            titulo = "error";
            detalle = msg("reservacion.error.noExiste");
        } catch (IllegalStateException e) {
            titulo = "error";
            detalle = msg("reservacion.error.asociada");
        }
        redirectAttributes.addFlashAttribute(titulo, detalle);
        return "redirect:/admin/reservaciones/listado";
    }

    @GetMapping("/reservaciones/confirmacion/{idReservacion}")
    public String confirmacion(@PathVariable Integer idReservacion, HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        var reservacionOpt = reservacionService.getReservacion(idReservacion);
        if (reservacionOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", msg("reservacion.error.noExiste"));
            return "redirect:/catalogo/listado";
        }
        Reservacion reservacion = reservacionOpt.get();
        if (!puedeVerReservacion(reservacion, session)) {
            redirectAttributes.addFlashAttribute("error", msg("error.recurso.acceso"));
            return "redirect:/mis-reservaciones";
        }
        model.addAttribute("reservacion", reservacion);
        model.addAttribute("detalles", reservacionService.obtenerDetalle(idReservacion));
        return "/reservaciones/confirmacion";
    }

    @GetMapping("/mis-reservaciones")
    public String indexMisReservaciones() {
        return "redirect:/mis-reservaciones/listado";
    }

    @GetMapping("/mis-reservaciones/listado")
    public String misReservaciones(HttpSession session, Model model) {
        Integer idUsuario = (Integer) session.getAttribute("idUsuario");
        var reservaciones = reservacionService.listarReservacionesPorUsuario(idUsuario);
        model.addAttribute("reservaciones", reservaciones);
        model.addAttribute("totalReservaciones", reservaciones.size());
        return "/reservaciones/mis-reservaciones";
    }

    private boolean puedeVerReservacion(Reservacion reservacion, HttpSession session) {
        if (tieneRol(session.getAttribute("rolesUsuario"), "ADMIN")) {
            return true;
        }
        Integer idUsuario = (Integer) session.getAttribute("idUsuario");
        return reservacion.getUsuario() != null
                && reservacion.getUsuario().getIdUsuario() != null
                && reservacion.getUsuario().getIdUsuario().equals(idUsuario);
    }

    private boolean tieneRol(Object roles, String esperado) {
        if (roles instanceof Collection<?> coleccion) {
            return coleccion.stream()
                    .map(String::valueOf)
                    .map(String::toUpperCase)
                    .anyMatch(rol -> rol.contains(esperado));
        }
        return false;
    }

    private String msg(String key) {
        return messageSource.getMessage(key, null, LocaleContextHolder.getLocale());
    }
}
