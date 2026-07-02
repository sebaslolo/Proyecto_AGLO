package com.Proyecto_Grupo_1.controller;

import com.Proyecto_Grupo_1.domain.Estado;
import com.Proyecto_Grupo_1.service.EstadoService;
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
@RequestMapping("/admin/estados")
public class EstadoController {

    private final EstadoService estadoService;

    @GetMapping
    public String index() {
        return "redirect:/admin/estados/listado";
    }

    @GetMapping("/listado")
    public String listado(Model model) {
        var estados = estadoService.getEstados(false);
        model.addAttribute("estados", estados);
        model.addAttribute("totalEstados", estados.size());
        return "/admin/estados/listado";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("estado", new Estado());
        return "/admin/estados/modifica";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid Estado estado, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "/admin/estados/modifica";
        }
        estadoService.save(estado);
        redirectAttributes.addFlashAttribute("todoOk", "Estado guardado correctamente.");
        return "redirect:/admin/estados/listado";
    }

    @PostMapping("/eliminar")
    public String eliminar(@RequestParam Integer idEstado, RedirectAttributes redirectAttributes) {
        String titulo = "todoOk";
        String detalle = "Estado eliminado correctamente.";
        try {
            estadoService.delete(idEstado);
        } catch (IllegalArgumentException | IllegalStateException e) {
            titulo = "error";
            detalle = e.getMessage();
        }
        redirectAttributes.addFlashAttribute(titulo, detalle);
        return "redirect:/admin/estados/listado";
    }

    @GetMapping("/modificar/{idEstado}")
    public String modificar(@PathVariable Integer idEstado, Model model, RedirectAttributes redirectAttributes) {
        var estadoOpt = estadoService.getEstado(idEstado);
        if (estadoOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Estado no encontrado.");
            return "redirect:/admin/estados/listado";
        }
        model.addAttribute("estado", estadoOpt.get());
        return "/admin/estados/modifica";
    }
}
