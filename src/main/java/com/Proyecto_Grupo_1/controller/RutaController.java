package com.Proyecto_Grupo_1.controller;

import com.Proyecto_Grupo_1.domain.Ruta;
import com.Proyecto_Grupo_1.service.RolService;
import com.Proyecto_Grupo_1.service.RutaService;
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
@RequestMapping("/admin/rutas")
public class RutaController {

    private final RutaService rutaService;
    private final RolService rolService;
    private final MessageSource messageSource;

    public RutaController(RutaService rutaService, RolService rolService, MessageSource messageSource) {
        this.rutaService = rutaService;
        this.rolService = rolService;
        this.messageSource = messageSource;
    }

    @GetMapping
    public String index() {
        return "redirect:/admin/rutas/listado";
    }

    @GetMapping("/listado")
    public String listado(Model model) {
        var rutas = rutaService.getRutas(false);
        model.addAttribute("rutas", rutas);
        model.addAttribute("totalRutas", rutas.size());
        model.addAttribute("roles", rolService.getRoles(false));
        return "/admin/rutas/listado";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("ruta", new Ruta());
        model.addAttribute("roles", rolService.getRoles(false));
        return "/admin/rutas/modifica";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid Ruta ruta, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("roles", rolService.getRoles(false));
            return "/admin/rutas/modifica";
        }
        rutaService.save(ruta);
        redirectAttributes.addFlashAttribute("todoOk", msg("ruta.mensaje.guardado"));
        return "redirect:/admin/rutas/listado";
    }

    @PostMapping("/eliminar")
    public String eliminar(@RequestParam Integer idRuta, RedirectAttributes redirectAttributes) {
        try {
            rutaService.delete(idRuta);
            redirectAttributes.addFlashAttribute("todoOk", msg("ruta.mensaje.eliminada"));
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", msg("ruta.error.noExiste"));
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", msg("ruta.error.asociada"));
        }
        return "redirect:/admin/rutas/listado";
    }

    @GetMapping("/modificar/{idRuta}")
    public String modificar(@PathVariable Integer idRuta, Model model, RedirectAttributes redirectAttributes) {
        var rutaOpt = rutaService.getRuta(idRuta);
        if (rutaOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", msg("ruta.error.noExiste"));
            return "redirect:/admin/rutas/listado";
        }
        model.addAttribute("ruta", rutaOpt.get());
        model.addAttribute("roles", rolService.getRoles(false));
        return "/admin/rutas/modifica";
    }

    private String msg(String key) {
        return messageSource.getMessage(key, null, LocaleContextHolder.getLocale());
    }
}
