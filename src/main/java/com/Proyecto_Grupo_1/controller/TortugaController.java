
package com.Proyecto_Grupo_1.controller;

import com.Proyecto_Grupo_1.domain.Tortuga;
import com.Proyecto_Grupo_1.service.EstadoService;
import com.Proyecto_Grupo_1.service.TortugaService;
import jakarta.validation.Valid;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/tortugas")
public class TortugaController {

    private final TortugaService tortugaService;
    private final EstadoService estadoService;
    private final MessageSource messageSource;

    public TortugaController(
            TortugaService tortugaService,
            EstadoService estadoService,
            MessageSource messageSource){

        this.tortugaService=tortugaService;
        this.estadoService=estadoService;
        this.messageSource=messageSource;

    }

    @GetMapping
    public String index(){
        return "redirect:/admin/tortugas/listado";
    }

    @GetMapping("/listado")
    public String listado(Model model){

        var tortugas=tortugaService.getTortugas(false);

        model.addAttribute("tortugas",tortugas);
        model.addAttribute("totalTortugas",tortugas.size());

        return "/admin/tortugas/listado";

    }

    @GetMapping("/nuevo")
    public String nuevo(Model model){

        model.addAttribute("tortuga",new Tortuga());

        cargarCatalogos(model);

        return "/admin/tortugas/modifica";

    }

    @PostMapping("/guardar")
    public String guardar(@Valid Tortuga tortuga,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes){

        if(bindingResult.hasErrors()){
            cargarCatalogos(model);
            return "/admin/tortugas/modifica";
        }

        tortugaService.save(tortuga);

        redirectAttributes.addFlashAttribute("todoOk",msg("tortuga.mensaje.guardado"));

        return "redirect:/admin/tortugas/listado";

    }

    @PostMapping("/eliminar")
    public String eliminar(@RequestParam String etiquetaTortuga,
            RedirectAttributes redirectAttributes){

        String titulo="todoOk";
        String detalle=msg("tortuga.mensaje.eliminado");

        try{
            tortugaService.delete(etiquetaTortuga);
        }catch(Exception e){
            titulo="error";
            detalle=msg("tortuga.error.noExiste");
        }

        redirectAttributes.addFlashAttribute(titulo,detalle);

        return "redirect:/admin/tortugas/listado";

    }

    @GetMapping("/modificar/{etiquetaTortuga}")
    public String modificar(@PathVariable String etiquetaTortuga,
            Model model,
            RedirectAttributes redirectAttributes){

        var tortuga=tortugaService.getTortuga(etiquetaTortuga);

        if(tortuga.isEmpty()){

            redirectAttributes.addFlashAttribute("error",msg("tortuga.error.noExiste"));

            return "redirect:/admin/tortugas/listado";

        }

        model.addAttribute("tortuga",tortuga.get());

        cargarCatalogos(model);

        return "/admin/tortugas/modifica";

    }

    private void cargarCatalogos(Model model){

        model.addAttribute("estados",estadoService.getEstados(false));

    }

    private String msg(String key){
        return messageSource.getMessage(key,null,LocaleContextHolder.getLocale());
    }

}
