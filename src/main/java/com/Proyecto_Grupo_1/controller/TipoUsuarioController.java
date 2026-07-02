package com.Proyecto_Grupo_1.controller;

import com.Proyecto_Grupo_1.domain.TipoUsuario;
import com.Proyecto_Grupo_1.service.TipoUsuarioService;
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
@RequestMapping("/admin/tipos-usuario")
public class TipoUsuarioController {

    private final TipoUsuarioService tipoUsuarioService;

    @GetMapping
    public String index() {
        return "redirect:/admin/tipos-usuario/listado";
    }

    @GetMapping("/listado")
    public String listado(Model model) {
        var tiposUsuario = tipoUsuarioService.getTiposUsuario(false);
        model.addAttribute("tiposUsuario", tiposUsuario);
        model.addAttribute("totalTiposUsuario", tiposUsuario.size());
        return "/admin/tipos-usuario/listado";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("tipoUsuario", new TipoUsuario());
        return "/admin/tipos-usuario/modifica";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid TipoUsuario tipoUsuario, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "/admin/tipos-usuario/modifica";
        }
        tipoUsuarioService.save(tipoUsuario);
        redirectAttributes.addFlashAttribute("todoOk", "Tipo de usuario guardado correctamente.");
        return "redirect:/admin/tipos-usuario/listado";
    }

    @PostMapping("/eliminar")
    public String eliminar(@RequestParam Integer idTipoUsuario, RedirectAttributes redirectAttributes) {
        String titulo = "todoOk";
        String detalle = "Tipo de usuario eliminado correctamente.";
        try {
            tipoUsuarioService.delete(idTipoUsuario);
        } catch (IllegalArgumentException | IllegalStateException e) {
            titulo = "error";
            detalle = e.getMessage();
        }
        redirectAttributes.addFlashAttribute(titulo, detalle);
        return "redirect:/admin/tipos-usuario/listado";
    }

    @GetMapping("/modificar/{idTipoUsuario}")
    public String modificar(@PathVariable Integer idTipoUsuario, Model model, RedirectAttributes redirectAttributes) {
        var tipoUsuarioOpt = tipoUsuarioService.getTipoUsuario(idTipoUsuario);
        if (tipoUsuarioOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Tipo de usuario no encontrado.");
            return "redirect:/admin/tipos-usuario/listado";
        }
        model.addAttribute("tipoUsuario", tipoUsuarioOpt.get());
        return "/admin/tipos-usuario/modifica";
    }
}
