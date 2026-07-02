package com.Proyecto_Grupo_1.controller;

import com.Proyecto_Grupo_1.domain.Estado;
import com.Proyecto_Grupo_1.service.EstadoService;
import com.Proyecto_Grupo_1.service.GuiaActividadService;
import com.Proyecto_Grupo_1.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/actividades/{idActividad}/guias")
public class GuiaActividadController {

    private final GuiaActividadService guiaActividadService;
    private final UsuarioService usuarioService;
    private final EstadoService estadoService;

    @GetMapping
    public String index(@PathVariable Integer idActividad) {
        return "redirect:/admin/actividades/" + idActividad + "/guias/listado";
    }

    @GetMapping("/listado")
    public String listado(@PathVariable Integer idActividad, Model model) {
        model.addAttribute("idActividad", idActividad);
        model.addAttribute("asignaciones", guiaActividadService.getAsignacionesPorActividad(idActividad));
        model.addAttribute("guias", usuarioService.listarGuias());
        return "/admin/actividades/guias/listado";
    }

    @PostMapping("/guardar")
    public String guardar(
            @PathVariable Integer idActividad,
            @RequestParam Integer idUsuario,
            @RequestParam Integer idEstado,
            RedirectAttributes redirectAttributes) {
        try {
            Estado estado = estadoService.obtenerEstado(idEstado);
            guiaActividadService.save(idActividad, idUsuario, estado);
            redirectAttributes.addFlashAttribute("todoOk", "Guia asignado correctamente.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/actividades/" + idActividad + "/guias/listado";
    }

    @PostMapping("/eliminar")
    public String eliminar(
            @PathVariable Integer idActividad,
            @RequestParam Integer idUsuario,
            RedirectAttributes redirectAttributes) {
        try {
            guiaActividadService.delete(idActividad, idUsuario);
            redirectAttributes.addFlashAttribute("todoOk", "Asignacion eliminada correctamente.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/actividades/" + idActividad + "/guias/listado";
    }
}
