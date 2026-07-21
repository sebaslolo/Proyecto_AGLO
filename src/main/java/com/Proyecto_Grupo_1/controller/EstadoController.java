package com.Proyecto_Grupo_1.controller;

import com.Proyecto_Grupo_1.domain.Estado;
import com.Proyecto_Grupo_1.service.EstadoService;
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
@RequestMapping("/admin/estados")
public class EstadoController {

    private final EstadoService estadoService;
    private final MessageSource messageSource;

    public EstadoController(EstadoService estadoService, MessageSource messageSource) {
        this.estadoService = estadoService;
        this.messageSource = messageSource;
    }

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
        redirectAttributes.addFlashAttribute("todoOk", msg("estado.mensaje.guardado"));
        return "redirect:/admin/estados/listado";
    }

    @PostMapping("/eliminar")
    public String eliminar(@RequestParam Integer idEstado, RedirectAttributes redirectAttributes) {
        String titulo = "todoOk";
        String detalle = msg("estado.mensaje.eliminado");
        try {
            estadoService.delete(idEstado);
        } catch (IllegalArgumentException e) {
            titulo = "error";
            detalle = msg("estado.error.noExiste");
        } catch (IllegalStateException e) {
            titulo = "error";
            detalle = msg("estado.error.asociado");
        }
        redirectAttributes.addFlashAttribute(titulo, detalle);
        return "redirect:/admin/estados/listado";
    }

    @GetMapping("/modificar/{idEstado}")
    public String modificar(@PathVariable Integer idEstado, Model model, RedirectAttributes redirectAttributes) {
        var estadoOpt = estadoService.getEstado(idEstado);
        if (estadoOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", msg("estado.error.noExiste"));
            return "redirect:/admin/estados/listado";
        }
        model.addAttribute("estado", estadoOpt.get());
        return "/admin/estados/modifica";
    }

    private String msg(String key) {
        return messageSource.getMessage(key, null, LocaleContextHolder.getLocale());
    }
}
