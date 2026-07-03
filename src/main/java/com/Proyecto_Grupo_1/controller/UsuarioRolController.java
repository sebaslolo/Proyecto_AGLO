package com.Proyecto_Grupo_1.controller;

import com.Proyecto_Grupo_1.service.RolService;
import com.Proyecto_Grupo_1.service.UsuarioRolService;
import com.Proyecto_Grupo_1.service.UsuarioService;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/usuarios-roles")
public class UsuarioRolController {

    private final UsuarioRolService usuarioRolService;
    private final UsuarioService usuarioService;
    private final RolService rolService;
    private final MessageSource messageSource;

    public UsuarioRolController(UsuarioRolService usuarioRolService,
            UsuarioService usuarioService,
            RolService rolService,
            MessageSource messageSource) {
        this.usuarioRolService = usuarioRolService;
        this.usuarioService = usuarioService;
        this.rolService = rolService;
        this.messageSource = messageSource;
    }

    @GetMapping
    public String index(@RequestParam(required = false) Integer idUsuario) {
        if (idUsuario != null) {
            return "redirect:/admin/usuarios-roles/listado?idUsuario=" + idUsuario;
        }
        return "redirect:/admin/usuarios-roles/listado";
    }

    @GetMapping("/listado")
    public String listado(@RequestParam(required = false) Integer idUsuario, Model model) {
        model.addAttribute("usuarios", usuarioService.getUsuarios(false));
        model.addAttribute("roles", rolService.getRoles(false));
        model.addAttribute("idUsuario", idUsuario);
        if (idUsuario != null) {
            model.addAttribute("asignaciones", usuarioRolService.getRolesPorUsuario(idUsuario));
        }
        return "/admin/usuarios-roles/listado";
    }

    @PostMapping("/guardar")
    public String guardar(@RequestParam Integer idUsuario, @RequestParam Integer idRol, RedirectAttributes redirectAttributes) {
        try {
            usuarioRolService.save(idUsuario, idRol);
            redirectAttributes.addFlashAttribute("todoOk", msg("rol.mensaje.asignado"));
        } catch (IllegalArgumentException | IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", msg("rol.error.duplicado"));
        }
        return "redirect:/admin/usuarios-roles/listado?idUsuario=" + idUsuario;
    }

    @PostMapping("/eliminar")
    public String eliminar(@RequestParam Integer idUsuario, @RequestParam Integer idRol, RedirectAttributes redirectAttributes) {
        try {
            usuarioRolService.delete(idUsuario, idRol);
            redirectAttributes.addFlashAttribute("todoOk", msg("rol.mensaje.removido"));
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", msg("rol.error.noExiste"));
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", msg("rol.error.asociado"));
        }
        return "redirect:/admin/usuarios-roles/listado?idUsuario=" + idUsuario;
    }

    private String msg(String key) {
        return messageSource.getMessage(key, null, LocaleContextHolder.getLocale());
    }
}
