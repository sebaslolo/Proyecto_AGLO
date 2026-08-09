package com.Proyecto_Grupo_1.controller;

import com.Proyecto_Grupo_1.domain.Usuario;
import com.Proyecto_Grupo_1.service.RetroalimentacionService;
import com.Proyecto_Grupo_1.service.UsuarioService;
import com.Proyecto_Grupo_1.service.VoluntariadoService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class VoluntariadoController {

    private final VoluntariadoService voluntariadoService;
    private final RetroalimentacionService retroalimentacionService;
    private final UsuarioService usuarioService;

    public VoluntariadoController(
            VoluntariadoService voluntariadoService,
            RetroalimentacionService retroalimentacionService,
            UsuarioService usuarioService) {

        this.voluntariadoService = voluntariadoService;
        this.retroalimentacionService = retroalimentacionService;
        this.usuarioService = usuarioService;
    }

    @GetMapping("/voluntariados/listado")
    public String listado() {
        return "/voluntariados/listado";
    }

    @GetMapping("/mis-voluntariados")
    public String indexMisVoluntariados() {
        return "redirect:/mis-voluntariados/listado";
    }

    @GetMapping("/mis-voluntariados/listado")
    public String misVoluntariados(
            Authentication authentication,
            Model model) {

        Usuario usuario = obtenerUsuarioActual(authentication);

        var voluntariados =
                voluntariadoService.getVoluntariadosPorUsuario(
                        usuario.getIdUsuario());

        model.addAttribute("voluntariados", voluntariados);
        model.addAttribute("totalVoluntariados", voluntariados.size());

        return "/voluntariados/mis-voluntariados";
    }

    private Usuario obtenerUsuarioActual(Authentication authentication) {
        return usuarioService
                .getUsuarioPorUsername(authentication.getName())
                .orElseThrow(() -> new IllegalStateException(
                        "Usuario autenticado no encontrado."));
    }
}