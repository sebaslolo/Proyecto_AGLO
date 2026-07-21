package com.Proyecto_Grupo_1.controller;

import com.Proyecto_Grupo_1.service.GuiaActividadService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class GuiaController {

    private final GuiaActividadService guiaActividadService;

    public GuiaController(GuiaActividadService guiaActividadService) {
        this.guiaActividadService = guiaActividadService;
    }

    @GetMapping("/guia/agenda")
    public String agenda(HttpSession session, Model model) {
        Integer idGuia = (Integer) session.getAttribute("idGuia");
        if (idGuia != null) {
            var asignaciones = guiaActividadService.getAsignacionesPorGuia(idGuia);
            model.addAttribute("asignaciones", asignaciones);
            model.addAttribute("totalAsignaciones", asignaciones.size());
        }
        return "/guia/agenda";
    }
}
