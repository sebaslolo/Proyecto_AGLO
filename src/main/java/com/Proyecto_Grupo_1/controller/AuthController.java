package com.Proyecto_Grupo_1.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthController {

    @GetMapping("/login")
    public String login() {
        return "/auth/login";
    }

    @GetMapping("/registro")
    public String registro() {
        return "/auth/registro";
    }

    @GetMapping("/forgot-password")
    public String forgotPassword() {
        return "/auth/forgot-password";
    }
}
