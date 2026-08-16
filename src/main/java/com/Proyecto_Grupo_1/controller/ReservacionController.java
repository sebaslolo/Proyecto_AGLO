package com.Proyecto_Grupo_1.controller;

import com.Proyecto_Grupo_1.domain.Reservacion;
import com.Proyecto_Grupo_1.domain.Usuario;
import com.Proyecto_Grupo_1.dto.EstadoReservacionForm;
import com.Proyecto_Grupo_1.dto.ReservacionAdminForm;
import com.Proyecto_Grupo_1.dto.ReservacionForm;
import com.Proyecto_Grupo_1.service.ActividadService;
import com.Proyecto_Grupo_1.service.EstadoService;
import com.Proyecto_Grupo_1.service.ReservacionService;
import com.Proyecto_Grupo_1.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ReservacionController {

    private final ReservacionService reservacionService;
    private final ActividadService actividadService;
    private final EstadoService estadoService;
    private final UsuarioService usuarioService;
    private final MessageSource messageSource;

    public ReservacionController(ReservacionService reservacionService,
            ActividadService actividadService,
            EstadoService estadoService,
            UsuarioService usuarioService,
            MessageSource messageSource) {
        this.reservacionService = reservacionService;
        this.actividadService = actividadService;
        this.estadoService = estadoService;
        this.usuarioService = usuarioService;
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
        return "admin/reservaciones/listado";
    }

    @GetMapping("/admin/reservaciones/nuevo")
    public String nuevoAdmin(Model model) {
        model.addAttribute("reservacionAdminForm", new ReservacionAdminForm());
        cargarCatalogosAltaAdmin(model);
        return "admin/reservaciones/modifica";
    }

    @PostMapping("/admin/reservaciones/guardar")
    public String guardarAdmin(
            @Valid @ModelAttribute("reservacionAdminForm") ReservacionAdminForm reservacionAdminForm,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            cargarCatalogosAltaAdmin(model);
            return "admin/reservaciones/modifica";
        }

        try {
            reservacionService.crearReservacionAdministrativa(
                    reservacionAdminForm.getIdUsuario(),
                    reservacionAdminForm.getIdActividad(),
                    reservacionAdminForm.getCantidadPersonas());
        } catch (IllegalArgumentException | IllegalStateException e) {
            bindingResult.reject("formulario.invalido", msg("reservacion.admin.error.formulario"));
            cargarCatalogosAltaAdmin(model);
            return "admin/reservaciones/modifica";
        }

        redirectAttributes.addFlashAttribute("todoOk", msg("reservacion.mensaje.guardada"));
        return "redirect:/admin/reservaciones/listado";
    }

    @GetMapping("/admin/reservaciones/modificar/{idReservacion}")
    public String modificarAdmin(
            @PathVariable Integer idReservacion,
            Model model,
            RedirectAttributes redirectAttributes) {
        var reservacionOpt = reservacionService.getReservacion(idReservacion);
        if (reservacionOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", msg("reservacion.error.noExiste"));
            return "redirect:/admin/reservaciones/listado";
        }

        Reservacion reservacion = reservacionOpt.get();
        EstadoReservacionForm estadoReservacionForm = new EstadoReservacionForm();
        if (reservacion.getEstado() != null) {
            estadoReservacionForm.setIdEstado(reservacion.getEstado().getIdEstado());
        }
        cargarFormularioEstadoAdmin(model, reservacion, estadoReservacionForm);
        return "admin/reservaciones/estado";
    }

    @PostMapping("/admin/reservaciones/modificar/{idReservacion}")
    public String actualizarEstadoAdmin(
            @PathVariable Integer idReservacion,
            @Valid @ModelAttribute("estadoReservacionForm") EstadoReservacionForm estadoReservacionForm,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {
        var reservacionOpt = reservacionService.getReservacion(idReservacion);
        if (reservacionOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", msg("reservacion.error.noExiste"));
            return "redirect:/admin/reservaciones/listado";
        }

        Reservacion reservacion = reservacionOpt.get();
        if (bindingResult.hasErrors()) {
            cargarFormularioEstadoAdmin(model, reservacion, estadoReservacionForm);
            return "admin/reservaciones/estado";
        }

        try {
            reservacionService.actualizarEstadoReservacion(idReservacion, estadoReservacionForm.getIdEstado());
        } catch (IllegalArgumentException | IllegalStateException e) {
            bindingResult.reject("formulario.invalido", msg("reservacion.admin.error.formulario"));
            cargarFormularioEstadoAdmin(model, reservacion, estadoReservacionForm);
            return "admin/reservaciones/estado";
        }

        redirectAttributes.addFlashAttribute("todoOk", msg("reservacion.mensaje.estadoActualizado"));
        return "redirect:/admin/reservaciones/listado";
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
        return "admin/reservaciones/detalle";
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
        return "reservaciones/modifica";
    }

    @PostMapping("/reservaciones/guardar")
    public String guardar(@Valid ReservacionForm reservacionForm,
            BindingResult bindingResult,
            Authentication authentication,
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
            return "reservaciones/modifica";
        }
        Integer idUsuario = obtenerUsuarioActual(authentication).getIdUsuario();
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
            return "reservaciones/modifica";
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
    public String confirmacion(@PathVariable Integer idReservacion,
            Authentication authentication,
            Model model,
            RedirectAttributes redirectAttributes) {
        var reservacionOpt = reservacionService.getReservacion(idReservacion);
        if (reservacionOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", msg("reservacion.error.noExiste"));
            return "redirect:/catalogo/listado";
        }
        Reservacion reservacion = reservacionOpt.get();
        if (!puedeVerReservacion(reservacion, authentication)) {
            redirectAttributes.addFlashAttribute("error", msg("error.recurso.acceso"));
            return "redirect:/mis-reservaciones";
        }
        model.addAttribute("reservacion", reservacion);
        model.addAttribute("detalles", reservacionService.obtenerDetalle(idReservacion));
        return "reservaciones/confirmacion";
    }

    @GetMapping("/mis-reservaciones")
    public String indexMisReservaciones() {
        return "redirect:/mis-reservaciones/listado";
    }

    @GetMapping("/mis-reservaciones/listado")
    public String misReservaciones(Authentication authentication, Model model) {
        Integer idUsuario = obtenerUsuarioActual(authentication).getIdUsuario();
        var reservaciones = reservacionService.listarReservacionesPorUsuario(idUsuario);
        model.addAttribute("reservaciones", reservaciones);
        model.addAttribute("totalReservaciones", reservaciones.size());
        return "reservaciones/mis-reservaciones";
    }

    private void cargarCatalogosAltaAdmin(Model model) {
        model.addAttribute("usuarios", usuarioService.listarClientesActivosParaReservacion());
        model.addAttribute("actividades", actividadService.getActividades(false));
    }

    private void cargarFormularioEstadoAdmin(
            Model model,
            Reservacion reservacion,
            EstadoReservacionForm estadoReservacionForm) {
        model.addAttribute("reservacion", reservacion);
        model.addAttribute("detalles", reservacionService.obtenerDetalle(reservacion.getIdReservacion()));
        model.addAttribute("estados", estadoService.getEstados(false));
        model.addAttribute("estadoReservacionForm", estadoReservacionForm);
    }

    private boolean puedeVerReservacion(Reservacion reservacion, Authentication authentication) {
        if (tieneRol(authentication, "ADMIN")) {
            return true;
        }
        Integer idUsuario = obtenerUsuarioActual(authentication).getIdUsuario();
        return reservacion.getUsuario() != null
                && reservacion.getUsuario().getIdUsuario() != null
                && reservacion.getUsuario().getIdUsuario().equals(idUsuario);
    }

    private boolean tieneRol(Authentication authentication, String esperado) {
        return authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_" + esperado));
    }

    private Usuario obtenerUsuarioActual(Authentication authentication) {
        return usuarioService.getUsuarioPorUsername(authentication.getName())
                .orElseThrow(() -> new IllegalStateException("Usuario autenticado no encontrado."));
    }

    private String msg(String key) {
        return messageSource.getMessage(key, null, LocaleContextHolder.getLocale());
    }
}
