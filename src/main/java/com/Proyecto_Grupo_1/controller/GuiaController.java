package com.Proyecto_Grupo_1.controller;

import com.Proyecto_Grupo_1.service.GuiaActividadService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class GuiaController {

    private final GuiaActividadService guiaActividadService;

    @GetMapping("/guia/agenda")
    public String agenda(@RequestParam(required = false) Integer idUsuario, Model model) {
        if (idUsuario != null) {
            model.addAttribute("asignaciones", guiaActividadService.getAsignacionesPorGuia(idUsuario));
        }
        return "/guia/agenda";
    }
}
