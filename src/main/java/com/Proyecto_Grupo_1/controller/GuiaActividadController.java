package com.Proyecto_Grupo_1.controller;

import com.Proyecto_Grupo_1.domain.Estado;
import com.Proyecto_Grupo_1.service.ActividadService;
import com.Proyecto_Grupo_1.service.EstadoService;
import com.Proyecto_Grupo_1.service.GuiaActividadService;
import com.Proyecto_Grupo_1.service.GuiaService;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/actividades/{idActividad}/guias")
public class GuiaActividadController {

    private final GuiaActividadService guiaActividadService;
    private final ActividadService actividadService;
    private final GuiaService guiaService;
    private final EstadoService estadoService;
    private final MessageSource messageSource;

    public GuiaActividadController(GuiaActividadService guiaActividadService,
            ActividadService actividadService,
            GuiaService guiaService,
            EstadoService estadoService,
            MessageSource messageSource) {
        this.guiaActividadService = guiaActividadService;
        this.actividadService = actividadService;
        this.guiaService = guiaService;
        this.estadoService = estadoService;
        this.messageSource = messageSource;
    }

    @GetMapping
    public String index(@PathVariable Integer idActividad) {
        return "redirect:/admin/actividades/" + idActividad + "/guias/listado";
    }

    @GetMapping("/listado")
    public String listado(@PathVariable Integer idActividad, Model model) {
        model.addAttribute("idActividad", idActividad);
        model.addAttribute("actividad", actividadService.obtenerActividad(idActividad));
        model.addAttribute("asignaciones", guiaActividadService.getAsignacionesPorActividad(idActividad));
        model.addAttribute("guias", guiaService.getGuias(false));
        model.addAttribute("estados", estadoService.getEstados(false));
        return "/admin/actividades/guias/listado";
    }

    @PostMapping("/guardar")
    public String guardar(
            @PathVariable Integer idActividad,
            @RequestParam Integer idGuia,
            @RequestParam Integer idEstado,
            RedirectAttributes redirectAttributes) {
        try {
            Estado estado = estadoService.obtenerEstado(idEstado);
            guiaActividadService.save(idActividad, idGuia, estado);
            redirectAttributes.addFlashAttribute("todoOk", msg("guiaActividad.mensaje.asignado"));
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", msg("guiaActividad.error.duplicada"));
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", msg("guiaActividad.error.inesperado"));
        }
        return "redirect:/admin/actividades/" + idActividad + "/guias/listado";
    }

    @PostMapping("/eliminar")
    public String eliminar(
            @PathVariable Integer idActividad,
            @RequestParam Integer idGuia,
            RedirectAttributes redirectAttributes) {
        try {
            guiaActividadService.delete(idActividad, idGuia);
            redirectAttributes.addFlashAttribute("todoOk", msg("guiaActividad.mensaje.eliminado"));
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", msg("guiaActividad.error.noExiste"));
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", msg("guiaActividad.error.asociado"));
        }
        return "redirect:/admin/actividades/" + idActividad + "/guias/listado";
    }

    private String msg(String key) {
        return messageSource.getMessage(key, null, LocaleContextHolder.getLocale());
    }
}
