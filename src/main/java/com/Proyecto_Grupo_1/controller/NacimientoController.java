package com.Proyecto_Grupo_1.controller;

import com.Proyecto_Grupo_1.domain.Nacimiento;
import com.Proyecto_Grupo_1.service.EstadoService;
import com.Proyecto_Grupo_1.service.NacimientoService;
import com.Proyecto_Grupo_1.service.NidoService;
import jakarta.validation.Valid;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
@RequestMapping("/admin/nacimientos")
public class NacimientoController {


    private final NacimientoService nacimientoService;
    private final NidoService nidoService;
    private final EstadoService estadoService;
    private final MessageSource messageSource;


    public NacimientoController(
            NacimientoService nacimientoService,
            NidoService nidoService,
            EstadoService estadoService,
            MessageSource messageSource){

        this.nacimientoService = nacimientoService;
        this.nidoService = nidoService;
        this.estadoService = estadoService;
        this.messageSource = messageSource;

    }



    @GetMapping
    public String index(){

        return "redirect:/admin/nacimientos/listado";

    }



    @GetMapping("/listado")
    public String listado(Model model){


        var nacimientos = nacimientoService.getNacimientos(false);


        model.addAttribute("nacimientos", nacimientos);
        model.addAttribute(
                "totalNacimientos",
                nacimientos.size()
        );


        return "/admin/nacimientos/listado";

    }



    @GetMapping("/nuevo")
    public String nuevo(Model model){


        model.addAttribute(
                "nacimiento",
                new Nacimiento()
        );


        cargarCatalogos(model);


        return "/admin/nacimientos/modifica";

    }




    @PostMapping("/guardar")
    public String guardar(
            @Valid Nacimiento nacimiento,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes){


        if(bindingResult.hasErrors()){

            cargarCatalogos(model);

            return "/admin/nacimientos/modifica";

        }


        try {
            nacimientoService.save(nacimiento);
        } catch (IllegalArgumentException e) {
            bindingResult.reject("formulario.invalido", e.getMessage());
            cargarCatalogos(model);
            return "/admin/nacimientos/modifica";
        }


        redirectAttributes.addFlashAttribute(
                "todoOk",
                msg("nacimiento.mensaje.guardado")
        );


        return "redirect:/admin/nacimientos/listado";

    }



    @PostMapping("/eliminar")
    public String eliminar(
            @RequestParam Integer idNacimiento,
            RedirectAttributes redirectAttributes){


        String titulo = "todoOk";
        String detalle = msg("nacimiento.mensaje.eliminado");


        try{

            nacimientoService.delete(idNacimiento);


        }catch(Exception e){

            titulo = "error";
            detalle = msg("nacimiento.error.noExiste");

        }


        redirectAttributes.addFlashAttribute(
                titulo,
                detalle
        );


        return "redirect:/admin/nacimientos/listado";

    }



    @GetMapping("/modificar/{idNacimiento}")
    public String modificar(
            @PathVariable Integer idNacimiento,
            Model model,
            RedirectAttributes redirectAttributes){


        var nacimiento =
                nacimientoService.getNacimiento(idNacimiento);



        if(nacimiento.isEmpty()){


            redirectAttributes.addFlashAttribute(
                    "error",
                    msg("nacimiento.error.noExiste")
            );


            return "redirect:/admin/nacimientos/listado";


        }



        model.addAttribute(
                "nacimiento",
                nacimiento.get()
        );


        cargarCatalogos(model);



        return "/admin/nacimientos/modifica";


    }



    private void cargarCatalogos(Model model){


        model.addAttribute(
                "nidos",
                nidoService.getNidos(false)
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
