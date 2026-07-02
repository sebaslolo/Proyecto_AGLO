package com.Proyecto_Grupo_1.controller;

import com.Proyecto_Grupo_1.domain.Actividad;
import com.Proyecto_Grupo_1.service.ActividadService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
@RequestMapping("/admin/actividades")
public class ActividadController {

    private final ActividadService actividadService;

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
        redirectAttributes.addFlashAttribute("todoOk", "Actividad guardada correctamente.");
        return "redirect:/admin/actividades/listado";
    }

    @PostMapping("/eliminar")
    public String eliminar(@RequestParam Integer idActividad, RedirectAttributes redirectAttributes) {
        String titulo = "todoOk";
        String detalle = "Actividad eliminada correctamente.";
        try {
            actividadService.delete(idActividad);
        } catch (IllegalArgumentException | IllegalStateException e) {
            titulo = "error";
            detalle = e.getMessage();
        }
        redirectAttributes.addFlashAttribute(titulo, detalle);
        return "redirect:/admin/actividades/listado";
    }

    @GetMapping("/modificar/{idActividad}")
    public String modificar(@PathVariable Integer idActividad, Model model, RedirectAttributes redirectAttributes) {
        var actividadOpt = actividadService.getActividad(idActividad);
        if (actividadOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Actividad no encontrada.");
            return "redirect:/admin/actividades/listado";
        }
        model.addAttribute("actividad", actividadOpt.get());
        return "/admin/actividades/modifica";
    }
}
