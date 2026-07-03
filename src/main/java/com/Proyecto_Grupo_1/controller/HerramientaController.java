package com.Proyecto_Grupo_1.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/herramientas")
public class HerramientaController {

    @GetMapping("/listado")
    public String listado() {
        return "/herramientas/listado";
    }
}
