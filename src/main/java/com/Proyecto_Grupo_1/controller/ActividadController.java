package com.Proyecto_Grupo_1.controller;

import com.Proyecto_Grupo_1.domain.Actividad;
import com.Proyecto_Grupo_1.service.ActividadService;
import jakarta.validation.Valid;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/actividades")
public class ActividadController {

    private final ActividadService actividadService;
    private final MessageSource messageSource;

    public ActividadController(ActividadService actividadService, MessageSource messageSource) {
        this.actividadService = actividadService;
        this.messageSource = messageSource;
    }

    @GetMapping
    public String index() {
        return "redirect:/admin/actividades/listado";
    }

    @GetMapping("/listado")
    public String listado(Model model) {
        var actividades = actividadService.getActividades(false);
        model.addAttribute("actividades", actividades);
        model.addAttribute("totalActividades", actividades.size());
        return "/admin/actividades/listado";
    }

    @GetMapping("/nueva")
    public String nueva(Model model) {
        model.addAttribute("actividad", new Actividad());
        return "/admin/actividades/modifica";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid Actividad actividad, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "/admin/actividades/modifica";
        }
        actividadService.save(actividad);
        redirectAttributes.addFlashAttribute("todoOk", msg("actividad.mensaje.guardado"));
        return "redirect:/admin/actividades/listado";
    }

    @PostMapping("/eliminar")
    public String eliminar(@RequestParam Integer idActividad, RedirectAttributes redirectAttributes) {
        String titulo = "todoOk";
        String detalle = msg("actividad.mensaje.eliminada");
        try {
            actividadService.delete(idActividad);
        } catch (IllegalArgumentException e) {
            titulo = "error";
            detalle = msg("actividad.error.noExiste");
        } catch (IllegalStateException e) {
            titulo = "error";
            detalle = msg("actividad.error.asociada");
        }
        redirectAttributes.addFlashAttribute(titulo, detalle);
        return "redirect:/admin/actividades/listado";
    }

    @GetMapping("/modificar/{idActividad}")
    public String modificar(@PathVariable Integer idActividad, Model model, RedirectAttributes redirectAttributes) {
        var actividadOpt = actividadService.getActividad(idActividad);
        if (actividadOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", msg("actividad.error.noExiste"));
            return "redirect:/admin/actividades/listado";
        }
        model.addAttribute("actividad", actividadOpt.get());
        return "/admin/actividades/modifica";
    }

    private String msg(String key) {
        return messageSource.getMessage(key, null, LocaleContextHolder.getLocale());
    }
}
