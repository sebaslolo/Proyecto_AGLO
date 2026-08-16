package com.Proyecto_Grupo_1.controller;

import com.Proyecto_Grupo_1.domain.Herramienta;
import com.Proyecto_Grupo_1.service.EstadoService;
import com.Proyecto_Grupo_1.service.HerramientaService;
import jakarta.validation.Valid;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/herramientas")
public class HerramientaController {

    private final HerramientaService herramientaService;
    private final EstadoService estadoService;
    private final MessageSource messageSource;

    public HerramientaController(HerramientaService herramientaService,
            EstadoService estadoService,
            MessageSource messageSource){

        this.herramientaService = herramientaService;
        this.estadoService = estadoService;
        this.messageSource = messageSource;

    }

    @GetMapping
    public String index(){
        return "redirect:/admin/herramientas/listado";
    }

    @GetMapping("/listado")
    public String listado(Model model){

        var herramientas = herramientaService.getHerramientas(false);

        model.addAttribute("herramientas", herramientas);
        model.addAttribute("totalHerramientas", herramientas.size());

        return "admin/herramientas/listado";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model){

        model.addAttribute("herramienta", new Herramienta());

        cargarCatalogos(model);

        return "admin/herramientas/modifica";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid Herramienta herramienta,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes){

        if(bindingResult.hasErrors()){
            cargarCatalogos(model);
            return "admin/herramientas/modifica";
        }

        try {
            herramientaService.save(herramienta);
        } catch (IllegalArgumentException e) {
            bindingResult.reject("formulario.invalido", e.getMessage());
            cargarCatalogos(model);
            return "admin/herramientas/modifica";
        }

        redirectAttributes.addFlashAttribute("todoOk",msg("herramienta.mensaje.guardado"));

        return "redirect:/admin/herramientas/listado";

    }

    @PostMapping("/eliminar")
    public String eliminar(@RequestParam Integer idHerramienta,
            RedirectAttributes redirectAttributes){

        String titulo="todoOk";
        String detalle=msg("herramienta.mensaje.eliminado");

        try{

            herramientaService.delete(idHerramienta);

        }catch(IllegalArgumentException e){

            titulo="error";
            detalle=msg("herramienta.error.noExiste");

        }catch(IllegalStateException e){

            titulo="error";
            detalle=msg("herramienta.error.asociado");

        }

        redirectAttributes.addFlashAttribute(titulo, detalle);

        return "redirect:/admin/herramientas/listado";

    }

    @GetMapping("/modificar/{idHerramienta}")
    public String modificar(@PathVariable Integer idHerramienta,
            Model model,
            RedirectAttributes redirectAttributes){

        var herramienta = herramientaService.getHerramienta(idHerramienta);

        if(herramienta.isEmpty()){

            redirectAttributes.addFlashAttribute("error",msg("herramienta.error.noExiste"));

            return "redirect:/admin/herramientas/listado";

        }

        model.addAttribute("herramienta", herramienta.get());

        cargarCatalogos(model);

        return "admin/herramientas/modifica";

    }

    private void cargarCatalogos(Model model){

        model.addAttribute("estados", estadoService.getEstados(false));

    }

    private String msg(String key){

        return messageSource.getMessage(key,null,LocaleContextHolder.getLocale());

    }

}
