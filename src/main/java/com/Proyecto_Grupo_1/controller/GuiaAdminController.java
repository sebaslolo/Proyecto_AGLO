package com.Proyecto_Grupo_1.controller;

import com.Proyecto_Grupo_1.domain.Guia;
import com.Proyecto_Grupo_1.service.EstadoService;
import com.Proyecto_Grupo_1.service.GuiaService;
import com.Proyecto_Grupo_1.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
@RequestMapping("/admin/guias")
public class GuiaAdminController {

    private final GuiaService guiaService;
    private final UsuarioService usuarioService;
    private final EstadoService estadoService;
    private final MessageSource messageSource;

    @GetMapping
    public String index() {
        return "redirect:/admin/guias/listado";
    }

    @GetMapping("/listado")
    public String listado(Model model) {
        var guias = guiaService.getGuias(false);
        model.addAttribute("guias", guias);
        model.addAttribute("totalGuias", guias.size());
        return "/admin/guias/listado";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("guia", new Guia());
        model.addAttribute("usuarios", usuarioService.getUsuarios(false));
        model.addAttribute("estados", estadoService.getEstados(false));
        return "/admin/guias/modifica";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid Guia guia, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("usuarios", usuarioService.getUsuarios(false));
            model.addAttribute("estados", estadoService.getEstados(false));
            return "/admin/guias/modifica";
        }
        guiaService.save(guia);
        redirectAttributes.addFlashAttribute("todoOk", msg("guia.mensaje.guardado"));
        return "redirect:/admin/guias/listado";
    }

    @PostMapping("/eliminar")
    public String eliminar(@RequestParam Integer idGuia, RedirectAttributes redirectAttributes) {
        try {
            guiaService.delete(idGuia);
            redirectAttributes.addFlashAttribute("todoOk", msg("guia.mensaje.eliminado"));
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", msg("guia.error.noExiste"));
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", msg("guia.error.asociado"));
        }
        return "redirect:/admin/guias/listado";
    }

    @GetMapping("/modificar/{idGuia}")
    public String modificar(@PathVariable Integer idGuia, Model model, RedirectAttributes redirectAttributes) {
        var guiaOpt = guiaService.getGuia(idGuia);
        if (guiaOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", msg("guia.error.noExiste"));
            return "redirect:/admin/guias/listado";
        }
        model.addAttribute("guia", guiaOpt.get());
        model.addAttribute("usuarios", usuarioService.getUsuarios(false));
        model.addAttribute("estados", estadoService.getEstados(false));
        return "/admin/guias/modifica";
    }

    private String msg(String key) {
        return messageSource.getMessage(key, null, LocaleContextHolder.getLocale());
    }
}
