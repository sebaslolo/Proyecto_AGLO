package com.Proyecto_Grupo_1.controller;

import com.Proyecto_Grupo_1.domain.Reservacion;
import com.Proyecto_Grupo_1.service.ReservacionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class ReservacionController {

    private final ReservacionService reservacionService;

    @GetMapping("/admin/reservaciones")
    public String indexAdmin() {
        return "redirect:/admin/reservaciones/listado";
    }

    @GetMapping("/admin/reservaciones/listado")
    public String listadoAdmin(Model model) {
        var reservaciones = reservacionService.getReservaciones(false);
        model.addAttribute("reservaciones", reservaciones);
        model.addAttribute("totalReservaciones", reservaciones.size());
        return "/admin/reservaciones/listado";
    }

    @GetMapping("/admin/reservaciones/detalle/{idReservacion}")
    public String detalleAdmin(@PathVariable Integer idReservacion, Model model, RedirectAttributes redirectAttributes) {
        var reservacionOpt = reservacionService.getReservacion(idReservacion);
        if (reservacionOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Reservacion no encontrada.");
            return "redirect:/admin/reservaciones/listado";
        }
        model.addAttribute("reservacion", reservacionOpt.get());
        model.addAttribute("detalles", reservacionService.obtenerDetalle(idReservacion));
        return "/admin/reservaciones/detalle";
    }

    @GetMapping("/reservaciones/nueva")
    public String nueva(Model model) {
        model.addAttribute("reservacion", new Reservacion());
        return "/reservaciones/modifica";
    }

    @PostMapping("/reservaciones/guardar")
    public String guardar(@Valid Reservacion reservacion, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "/reservaciones/modifica";
        }
        Reservacion guardada = reservacionService.save(reservacion);
        redirectAttributes.addFlashAttribute("todoOk", "Reservacion guardada correctamente.");
        return "redirect:/reservaciones/confirmacion/" + guardada.getIdReservacion();
    }

    @PostMapping("/admin/reservaciones/eliminar")
    public String eliminar(@RequestParam Integer idReservacion, RedirectAttributes redirectAttributes) {
        String titulo = "todoOk";
        String detalle = "Reservacion eliminada correctamente.";
        try {
            reservacionService.delete(idReservacion);
        } catch (IllegalArgumentException | IllegalStateException e) {
            titulo = "error";
            detalle = e.getMessage();
        }
        redirectAttributes.addFlashAttribute(titulo, detalle);
        return "redirect:/admin/reservaciones/listado";
    }

    @GetMapping("/reservaciones/confirmacion/{idReservacion}")
    public String confirmacion(@PathVariable Integer idReservacion, Model model, RedirectAttributes redirectAttributes) {
        var reservacionOpt = reservacionService.getReservacion(idReservacion);
        if (reservacionOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Reservacion no encontrada.");
            return "redirect:/catalogo/listado";
        }
        model.addAttribute("reservacion", reservacionOpt.get());
        model.addAttribute("detalles", reservacionService.obtenerDetalle(idReservacion));
        return "/reservaciones/confirmacion";
    }

    @GetMapping("/mis-reservaciones")
    public String indexMisReservaciones(@RequestParam Integer idUsuario) {
        return "redirect:/mis-reservaciones/listado?idUsuario=" + idUsuario;
    }

    @GetMapping("/mis-reservaciones/listado")
    public String misReservaciones(@RequestParam Integer idUsuario, Model model) {
        var reservaciones = reservacionService.listarReservacionesPorUsuario(idUsuario);
        model.addAttribute("reservaciones", reservaciones);
        model.addAttribute("totalReservaciones", reservaciones.size());
        return "/reservaciones/mis-reservaciones";
    }
}
