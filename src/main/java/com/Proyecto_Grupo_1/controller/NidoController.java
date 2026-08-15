package com.Proyecto_Grupo_1.controller;

import com.Proyecto_Grupo_1.domain.Nido;
import com.Proyecto_Grupo_1.service.EstadoService;
import com.Proyecto_Grupo_1.service.NidoService;
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
@RequestMapping("/admin/nidos")
public class NidoController {

    private final NidoService nidoService;
    private final TortugaService tortugaService;
    private final EstadoService estadoService;
    private final MessageSource messageSource;


    public NidoController(
            NidoService nidoService,
            TortugaService tortugaService,
            EstadoService estadoService,
            MessageSource messageSource){

        this.nidoService = nidoService;
        this.tortugaService = tortugaService;
        this.estadoService = estadoService;
        this.messageSource = messageSource;

    }


    @GetMapping
    public String index(){
        return "redirect:/admin/nidos/listado";
    }


    @GetMapping("/listado")
    public String listado(Model model){

        var nidos = nidoService.getNidos(false);

        model.addAttribute("nidos", nidos);
        model.addAttribute("totalNidos", nidos.size());

        return "/admin/nidos/listado";

    }


    @GetMapping("/nuevo")
    public String nuevo(Model model){

        model.addAttribute("nido", new Nido());

        cargarCatalogos(model);

        return "/admin/nidos/modifica";

    }


    @PostMapping("/guardar")
    public String guardar(
            @Valid Nido nido,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes){


        if(bindingResult.hasErrors()){

            cargarCatalogos(model);

            return "/admin/nidos/modifica";

        }


        try {
            nidoService.save(nido);
        } catch (IllegalArgumentException e) {
            bindingResult.reject("formulario.invalido", e.getMessage());
            cargarCatalogos(model);
            return "/admin/nidos/modifica";
        }

        redirectAttributes.addFlashAttribute(
                "todoOk",
                msg("nido.mensaje.guardado")
        );


        return "redirect:/admin/nidos/listado";

    }


    @PostMapping("/eliminar")
    public String eliminar(
            @RequestParam Integer idNido,
            RedirectAttributes redirectAttributes){


        String titulo = "todoOk";
        String detalle = msg("nido.mensaje.eliminado");


        try{

            nidoService.delete(idNido);

        }catch(Exception e){

            titulo = "error";
            detalle = msg("nido.error.noExiste");

        }


        redirectAttributes.addFlashAttribute(titulo, detalle);


        return "redirect:/admin/nidos/listado";

    }


    @GetMapping("/modificar/{idNido}")
    public String modificar(
            @PathVariable Integer idNido,
            Model model,
            RedirectAttributes redirectAttributes){


        var nido = nidoService.getNido(idNido);


        if(nido.isEmpty()){

            redirectAttributes.addFlashAttribute(
                    "error",
                    msg("nido.error.noExiste")
            );

            return "redirect:/admin/nidos/listado";

        }


        model.addAttribute("nido", nido.get());

        cargarCatalogos(model);


        return "/admin/nidos/modifica";

    }



    private void cargarCatalogos(Model model){

        model.addAttribute(
                "tortugas",
                tortugaService.getTortugas(false)
        );

        model.addAttribute(
                "estados",
                estadoService.getEstados(false)
        );

    }



    private String msg(String key){

        return messageSource.getMessage(
                key,
                null,
                LocaleContextHolder.getLocale()
        );

    }

}
