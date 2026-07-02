package com.Proyecto_Grupo_1.controller;

import com.Proyecto_Grupo_1.domain.Rol;
import com.Proyecto_Grupo_1.service.RolService;
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
@RequestMapping("/admin/roles")
public class RolController {

    private final RolService rolService;
    private final MessageSource messageSource;

    @GetMapping
    public String index() {
        return "redirect:/admin/roles/listado";
    }

    @GetMapping("/listado")
    public String listado(Model model) {
        var roles = rolService.getRoles(false);
        model.addAttribute("roles", roles);
        model.addAttribute("totalRoles", roles.size());
        return "/admin/roles/listado";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("rol", new Rol());
        return "/admin/roles/modifica";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid Rol rol, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "/admin/roles/modifica";
        }
        rolService.save(rol);
        redirectAttributes.addFlashAttribute("todoOk", msg("rol.mensaje.guardado"));
        return "redirect:/admin/roles/listado";
    }

    @PostMapping("/eliminar")
    public String eliminar(@RequestParam Integer idRol, RedirectAttributes redirectAttributes) {
        try {
            rolService.delete(idRol);
            redirectAttributes.addFlashAttribute("todoOk", msg("rol.mensaje.eliminado"));
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", msg("rol.error.noExiste"));
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", msg("rol.error.asociado"));
        }
        return "redirect:/admin/roles/listado";
    }

    @GetMapping("/modificar/{idRol}")
    public String modificar(@PathVariable Integer idRol, Model model, RedirectAttributes redirectAttributes) {
        var rolOpt = rolService.getRol(idRol);
        if (rolOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", msg("rol.error.noExiste"));
            return "redirect:/admin/roles/listado";
        }
        model.addAttribute("rol", rolOpt.get());
        return "/admin/roles/modifica";
    }

    private String msg(String key) {
        return messageSource.getMessage(key, null, LocaleContextHolder.getLocale());
    }
}
