package com.Proyecto_Grupo_1.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/avistamientos")
public class AvistamientoController {

    @GetMapping("/listado")
    public String listado() {
        // TODO: Crear tablas, entidades, repositorios y servicios para avistamientos.
        return "/avistamientos/listado";
    }
}
