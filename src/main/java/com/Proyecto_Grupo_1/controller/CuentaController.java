package com.Proyecto_Grupo_1.controller;

import com.Proyecto_Grupo_1.domain.Cuenta;
import com.Proyecto_Grupo_1.service.CuentaService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@RequestMapping("/admin/cuentas")
public class CuentaController {

    private final CuentaService cuentaService;

    public CuentaController(CuentaService cuentaService) {
        this.cuentaService = cuentaService;
    }

    @GetMapping
    public String index() {
        return "redirect:/admin/cuentas/listado";
    }

    @GetMapping("/listado")
    public String listado(Model model) {
        var cuentas = cuentaService.getCuentas(false);
        model.addAttribute("cuentas", cuentas);
        model.addAttribute("totalCuentas", cuentas.size());
        return "/admin/cuentas/listado";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("cuenta", new Cuenta());
        return "/admin/cuentas/modifica";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid Cuenta cuenta, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "/admin/cuentas/modifica";
        }
        cuentaService.save(cuenta);
        redirectAttributes.addFlashAttribute("todoOk", "Cuenta guardada correctamente.");
        return "redirect:/admin/cuentas/listado";
    }

    @PostMapping("/eliminar")
    public String eliminar(@RequestParam Integer idCuenta, RedirectAttributes redirectAttributes) {
        String titulo = "todoOk";
        String detalle = "Cuenta eliminada correctamente.";
        try {
            cuentaService.delete(idCuenta);
        } catch (IllegalArgumentException | IllegalStateException e) {
            titulo = "error";
            detalle = e.getMessage();
        }
        redirectAttributes.addFlashAttribute(titulo, detalle);
        return "redirect:/admin/cuentas/listado";
    }

    @GetMapping("/modificar/{idCuenta}")
    public String modificar(@PathVariable Integer idCuenta, Model model, RedirectAttributes redirectAttributes) {
        var cuentaOpt = cuentaService.getCuenta(idCuenta);
        if (cuentaOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Cuenta no encontrada.");
            return "redirect:/admin/cuentas/listado";
        }
        model.addAttribute("cuenta", cuentaOpt.get());
        return "/admin/cuentas/modifica";
    }
}
