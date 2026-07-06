package com.Proyecto_Grupo_1.controller;

import com.Proyecto_Grupo_1.domain.Telefono;
import com.Proyecto_Grupo_1.service.TelefonoService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@RequestMapping("/admin/telefonos")
public class TelefonoController {

    private final TelefonoService telefonoService;

    public TelefonoController(TelefonoService telefonoService) {
        this.telefonoService = telefonoService;
    }

    @GetMapping
    public String index() {
        return "redirect:/admin/telefonos/listado";
    }

    @GetMapping("/listado")
    public String listado(Model model) {
        var telefonos = telefonoService.getTelefonos(false);
        model.addAttribute("telefonos", telefonos);
        model.addAttribute("totalTelefonos", telefonos.size());
        return "/admin/telefonos/listado";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("telefono", new Telefono());
        return "/admin/telefonos/modifica";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid Telefono telefono, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "/admin/telefonos/modifica";
        }
        telefonoService.save(telefono);
        redirectAttributes.addFlashAttribute("todoOk", "Telefono guardado correctamente.");
        return "redirect:/admin/telefonos/listado";
    }

    @PostMapping("/eliminar")
    public String eliminar(@RequestParam Integer idTelefono, RedirectAttributes redirectAttributes) {
        String titulo = "todoOk";
        String detalle = "Telefono eliminado correctamente.";
        try {
            telefonoService.delete(idTelefono);
        } catch (IllegalArgumentException | IllegalStateException e) {
            titulo = "error";
            detalle = e.getMessage();
        }
        redirectAttributes.addFlashAttribute(titulo, detalle);
        return "redirect:/admin/telefonos/listado";
    }

    @GetMapping("/modificar/{idTelefono}")
    public String modificar(@PathVariable Integer idTelefono, Model model, RedirectAttributes redirectAttributes) {
        var telefonoOpt = telefonoService.getTelefono(idTelefono);
        if (telefonoOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Telefono no encontrado.");
            return "redirect:/admin/telefonos/listado";
        }
        model.addAttribute("telefono", telefonoOpt.get());
        return "/admin/telefonos/modifica";
    }
}
