package com.Proyecto_Grupo_1.controller;

import com.Proyecto_Grupo_1.service.ActividadService;
import com.Proyecto_Grupo_1.service.GuiaActividadService;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/catalogo")
public class CatalogoController {

    private final ActividadService actividadService;
    private final GuiaActividadService guiaActividadService;
    private final MessageSource messageSource;

    public CatalogoController(ActividadService actividadService,
            GuiaActividadService guiaActividadService,
            MessageSource messageSource) {
        this.actividadService = actividadService;
        this.guiaActividadService = guiaActividadService;
        this.messageSource = messageSource;
    }

    @GetMapping
    public String index() {
        return "redirect:/catalogo/listado";
    }

    @GetMapping("/listado")
    public String listado(Model model) {
        model.addAttribute("actividades", actividadService.getActividades(true));
        return "/catalogo/listado";
    }

    @GetMapping("/detalle/{idActividad}")
    public String detalle(@PathVariable Integer idActividad, Model model, RedirectAttributes redirectAttributes) {
        var actividadOpt = actividadService.getActividad(idActividad);
        if (actividadOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", msg("actividad.error.noExiste"));
            return "redirect:/catalogo/listado";
        }
        model.addAttribute("actividad", actividadOpt.get());
        model.addAttribute("guias", guiaActividadService.getAsignacionesPorActividad(idActividad));
        return "/catalogo/detalle";
    }

    private String msg(String key) {
        return messageSource.getMessage(key, null, LocaleContextHolder.getLocale());
    }
}
