package com.Proyecto_Grupo_1.controller;

import com.Proyecto_Grupo_1.domain.TipoActividad;
import com.Proyecto_Grupo_1.service.TipoActividadService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/tipos-actividad")
public class TipoActividadController {

    private final TipoActividadService tipoActividadService;
    private final MessageSource messageSource;

    @GetMapping
    public String index() {
        return "redirect:/admin/tipos-actividad/listado";
    }

    @GetMapping("/listado")
    public String listado(Model model) {
        var tiposActividad = tipoActividadService.getTiposActividad(false);
        model.addAttribute("tiposActividad", tiposActividad);
        model.addAttribute("totalTiposActividad", tiposActividad.size());
        return "/admin/tipos-actividad/listado";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("tipoActividad", new TipoActividad());
        return "/admin/tipos-actividad/modifica";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid TipoActividad tipoActividad, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "/admin/tipos-actividad/modifica";
        }
        tipoActividadService.save(tipoActividad);
        redirectAttributes.addFlashAttribute("todoOk", msg("tipoActividad.mensaje.guardado"));
        return "redirect:/admin/tipos-actividad/listado";
    }

    @PostMapping("/eliminar")
    public String eliminar(@RequestParam Integer idTipoActividad, RedirectAttributes redirectAttributes) {
        String titulo = "todoOk";
        String detalle = msg("tipoActividad.mensaje.eliminado");
        try {
            tipoActividadService.delete(idTipoActividad);
        } catch (IllegalArgumentException e) {
            titulo = "error";
            detalle = msg("tipoActividad.error.noExiste");
        } catch (IllegalStateException e) {
            titulo = "error";
            detalle = msg("tipoActividad.error.asociado");
        }
        redirectAttributes.addFlashAttribute(titulo, detalle);
        return "redirect:/admin/tipos-actividad/listado";
    }

    @GetMapping("/modificar/{idTipoActividad}")
    public String modificar(@PathVariable Integer idTipoActividad, Model model, RedirectAttributes redirectAttributes) {
        var tipoActividadOpt = tipoActividadService.getTipoActividad(idTipoActividad);
        if (tipoActividadOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", msg("tipoActividad.error.noExiste"));
            return "redirect:/admin/tipos-actividad/listado";
        }
        model.addAttribute("tipoActividad", tipoActividadOpt.get());
        return "/admin/tipos-actividad/modifica";
    }

    private String msg(String key) {
        return messageSource.getMessage(key, null, LocaleContextHolder.getLocale());
    }
}
