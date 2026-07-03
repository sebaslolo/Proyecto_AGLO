package com.Proyecto_Grupo_1.controller;

import com.Proyecto_Grupo_1.domain.Usuario;
import com.Proyecto_Grupo_1.service.UsuarioService;
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
@RequestMapping("/admin/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final MessageSource messageSource;

    public UsuarioController(UsuarioService usuarioService, MessageSource messageSource) {
        this.usuarioService = usuarioService;
        this.messageSource = messageSource;
    }

    @GetMapping
    public String index() {
        return "redirect:/admin/usuarios/listado";
    }

    @GetMapping("/listado")
    public String listado(@RequestParam(required = false) String q, Model model) {
        var usuarios = usuarioService.buscarUsuarios(q);
        model.addAttribute("usuarios", usuarios);
        model.addAttribute("totalUsuarios", usuarios.size());
        model.addAttribute("q", q);
        return "/admin/usuarios/listado";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "/admin/usuarios/modifica";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid Usuario usuario, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "/admin/usuarios/modifica";
        }
        usuarioService.save(usuario);
        redirectAttributes.addFlashAttribute("todoOk", msg("usuario.mensaje.guardado"));
        return "redirect:/admin/usuarios/listado";
    }

    @PostMapping("/eliminar")
    public String eliminar(@RequestParam Integer idUsuario, RedirectAttributes redirectAttributes) {
        String titulo = "todoOk";
        String detalle = msg("usuario.mensaje.eliminado");
        try {
            usuarioService.delete(idUsuario);
        } catch (IllegalArgumentException e) {
            titulo = "error";
            detalle = msg("usuario.error.noExiste");
        } catch (IllegalStateException e) {
            titulo = "error";
            detalle = msg("usuario.error.asociado");
        }
        redirectAttributes.addFlashAttribute(titulo, detalle);
        return "redirect:/admin/usuarios/listado";
    }

    @GetMapping("/modificar/{idUsuario}")
    public String modificar(@PathVariable Integer idUsuario, Model model, RedirectAttributes redirectAttributes) {
        var usuarioOpt = usuarioService.getUsuario(idUsuario);
        if (usuarioOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", msg("usuario.error.noExiste"));
            return "redirect:/admin/usuarios/listado";
        }
        model.addAttribute("usuario", usuarioOpt.get());
        return "/admin/usuarios/modifica";
    }

    private String msg(String key) {
        return messageSource.getMessage(key, null, LocaleContextHolder.getLocale());
    }
}
