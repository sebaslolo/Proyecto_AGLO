package com.Proyecto_Grupo_1.controller;

import com.Proyecto_Grupo_1.service.DashboardService;
import java.util.Locale;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/admin/dashboard")
    public String dashboard(Model model, Locale locale) {
        model.addAttribute("dashboard", dashboardService.obtenerResumen(locale));
        return "/admin/dashboard";
    }
}
