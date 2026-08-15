package com.Proyecto_Grupo_1.controller;

import com.Proyecto_Grupo_1.domain.Monitoreo;
import com.Proyecto_Grupo_1.service.EstadoService;
import com.Proyecto_Grupo_1.service.GuiaService;
import com.Proyecto_Grupo_1.service.MonitoreoService;
import jakarta.validation.Valid;
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
@RequestMapping("/admin/monitoreos")
public class MonitoreoController {

    private final MonitoreoService monitoreoService;
    private final GuiaService guiaService;
    private final EstadoService estadoService;
    private final MessageSource messageSource;

    public MonitoreoController(
            MonitoreoService monitoreoService,
            GuiaService guiaService,
            EstadoService estadoService,
            MessageSource messageSource) {

        this.monitoreoService = monitoreoService;
        this.guiaService = guiaService;
        this.estadoService = estadoService;
        this.messageSource = messageSource;

    }

    @GetMapping
    public String index() {
        return "redirect:/admin/monitoreos/listado";
    }

    @GetMapping("/listado")
    public String listado(Model model) {

        var monitoreos = monitoreoService.getMonitoreos(false);

        model.addAttribute("monitoreos", monitoreos);
        model.addAttribute("totalMonitoreos", monitoreos.size());

        return "/admin/monitoreos/listado";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {

        model.addAttribute("monitoreo", new Monitoreo());

        cargarCatalogos(model);

        return "/admin/monitoreos/modifica";
    }

    @PostMapping("/guardar")
    public String guardar(
            @Valid Monitoreo monitoreo,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {

            cargarCatalogos(model);

            return "/admin/monitoreos/modifica";
        }

        try {
            monitoreoService.save(monitoreo);
        } catch (IllegalArgumentException e) {
            bindingResult.reject("formulario.invalido", e.getMessage());
            cargarCatalogos(model);
            return "/admin/monitoreos/modifica";
        }

        redirectAttributes.addFlashAttribute(
                "todoOk",
                msg("monitoreo.mensaje.guardado"));

        return "redirect:/admin/monitoreos/listado";

    }

    @PostMapping("/eliminar")
    public String eliminar(
            @RequestParam Integer idMonitoreo,
            RedirectAttributes redirectAttributes) {

        String titulo = "todoOk";
        String detalle = msg("monitoreo.mensaje.eliminado");

        try {

            monitoreoService.delete(idMonitoreo);

        } catch (IllegalArgumentException e) {

            titulo = "error";
            detalle = msg("monitoreo.error.noExiste");

        } catch (IllegalStateException e) {

            titulo = "error";
            detalle = msg("monitoreo.error.asociado");

        }

        redirectAttributes.addFlashAttribute(titulo, detalle);

        return "redirect:/admin/monitoreos/listado";

    }

    @GetMapping("/modificar/{idMonitoreo}")
    public String modificar(
            @PathVariable Integer idMonitoreo,
            Model model,
            RedirectAttributes redirectAttributes) {

        var monitoreo = monitoreoService.getMonitoreo(idMonitoreo);

        if (monitoreo.isEmpty()) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    msg("monitoreo.error.noExiste"));

            return "redirect:/admin/monitoreos/listado";
        }

        model.addAttribute("monitoreo", monitoreo.get());

        cargarCatalogos(model);

        return "/admin/monitoreos/modifica";

    }

    private void cargarCatalogos(Model model) {

        model.addAttribute("guias", guiaService.getGuias(false));
        model.addAttribute("estados", estadoService.getEstados(false));

    }

    private String msg(String key) {

        return messageSource.getMessage(
                key,
                null,
                LocaleContextHolder.getLocale());

    }

}
