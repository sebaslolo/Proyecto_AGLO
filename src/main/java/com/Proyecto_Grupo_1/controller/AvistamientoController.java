package com.Proyecto_Grupo_1.controller;

import com.Proyecto_Grupo_1.domain.Avistamiento;
import com.Proyecto_Grupo_1.service.AvistamientoService;
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
@RequestMapping("/admin/avistamientos")
public class AvistamientoController {

    private final AvistamientoService avistamientoService;
    private final TortugaService tortugaService;
    private final EstadoService estadoService;
    private final MessageSource messageSource;

    public AvistamientoController(
            AvistamientoService avistamientoService,
            TortugaService tortugaService,
            EstadoService estadoService,
            MessageSource messageSource){

        this.avistamientoService=avistamientoService;
        this.tortugaService=tortugaService;
        this.estadoService=estadoService;
        this.messageSource=messageSource;

    }

    @GetMapping
    public String index(){
        return "redirect:/admin/avistamientos/listado";
    }

    @GetMapping("/listado")
    public String listado(Model model){

        var avistamientos=avistamientoService.getAvistamientos(false);

        model.addAttribute("avistamientos",avistamientos);
        model.addAttribute("totalAvistamientos",avistamientos.size());

        return "admin/avistamientos/listado";

    }

    @GetMapping("/nuevo")
    public String nuevo(Model model){

        model.addAttribute("avistamiento",new Avistamiento());

        cargarCatalogos(model);

        return "admin/avistamientos/modifica";

    }

    @PostMapping("/guardar")
    public String guardar(@Valid Avistamiento avistamiento,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes){

        if(bindingResult.hasErrors()){
            cargarCatalogos(model);
            return "admin/avistamientos/modifica";
        }

        try {
            avistamientoService.save(avistamiento);
        } catch (IllegalArgumentException e) {
            bindingResult.reject("formulario.invalido", e.getMessage());
            cargarCatalogos(model);
            return "admin/avistamientos/modifica";
        }

        redirectAttributes.addFlashAttribute("todoOk",msg("avistamiento.mensaje.guardado"));

        return "redirect:/admin/avistamientos/listado";

    }

    @PostMapping("/eliminar")
    public String eliminar(@RequestParam Integer idAvistamiento,
            RedirectAttributes redirectAttributes){

        String titulo="todoOk";
        String detalle=msg("avistamiento.mensaje.eliminado");

        try{
            avistamientoService.delete(idAvistamiento);
        }catch(Exception e){
            titulo="error";
            detalle=msg("avistamiento.error.noExiste");
        }

        redirectAttributes.addFlashAttribute(titulo,detalle);

        return "redirect:/admin/avistamientos/listado";

    }

    @GetMapping("/modificar/{idAvistamiento}")
    public String modificar(@PathVariable Integer idAvistamiento,
            Model model,
            RedirectAttributes redirectAttributes){

        var avistamiento=avistamientoService.getAvistamiento(idAvistamiento);

        if(avistamiento.isEmpty()){

            redirectAttributes.addFlashAttribute("error",msg("avistamiento.error.noExiste"));

            return "redirect:/admin/avistamientos/listado";

        }

        model.addAttribute("avistamiento",avistamiento.get());

        cargarCatalogos(model);

        return "admin/avistamientos/modifica";

    }

    private void cargarCatalogos(Model model){

        model.addAttribute("tortugas",tortugaService.getTortugas(false));
        model.addAttribute("estados",estadoService.getEstados(false));

    }

    private String msg(String key){
        return messageSource.getMessage(key,null,LocaleContextHolder.getLocale());
    }

}
