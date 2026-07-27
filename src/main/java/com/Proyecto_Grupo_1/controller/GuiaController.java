package com.Proyecto_Grupo_1.controller;

import com.Proyecto_Grupo_1.service.GuiaActividadService;
import com.Proyecto_Grupo_1.service.GuiaService;
import com.Proyecto_Grupo_1.service.UsuarioService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class GuiaController {

    private final GuiaActividadService guiaActividadService;
    private final GuiaService guiaService;
    private final UsuarioService usuarioService;

    public GuiaController(GuiaActividadService guiaActividadService,
            GuiaService guiaService,
            UsuarioService usuarioService) {
        this.guiaActividadService = guiaActividadService;
        this.guiaService = guiaService;
        this.usuarioService = usuarioService;
    }

    @GetMapping("/guia/agenda")
    public String agenda(Authentication authentication, Model model) {
        usuarioService.getUsuarioPorUsername(authentication.getName())
                .flatMap(usuario -> guiaService.getGuiaPorUsuario(usuario.getIdUsuario()))
                .ifPresent(guia -> {
                    var asignaciones = guiaActividadService.getAsignacionesPorGuia(guia.getIdGuia());
                    model.addAttribute("asignaciones", asignaciones);
                    model.addAttribute("totalAsignaciones", asignaciones.size());
                });
        return "/guia/agenda";
    }
}
