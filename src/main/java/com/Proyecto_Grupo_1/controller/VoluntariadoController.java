package com.Proyecto_Grupo_1.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/voluntariados")
public class VoluntariadoController {

    @GetMapping("/listado")
    public String listado() {
        // TODO: Crear tablas, entidades, repositorios y servicios para voluntariados.
        return "/voluntariados/listado";
    }
}
