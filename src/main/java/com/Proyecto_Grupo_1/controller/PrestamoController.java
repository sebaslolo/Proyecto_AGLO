package com.Proyecto_Grupo_1.controller;

import com.Proyecto_Grupo_1.domain.Prestamo;
import com.Proyecto_Grupo_1.service.EstadoService;
import com.Proyecto_Grupo_1.service.HerramientaService;
import com.Proyecto_Grupo_1.service.PrestamoService;
import com.Proyecto_Grupo_1.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/prestamos")
public class PrestamoController {

    private final PrestamoService prestamoService;
    private final HerramientaService herramientaService;
    private final UsuarioService usuarioService;
    private final EstadoService estadoService;
    private final MessageSource messageSource;

    public PrestamoController(
            PrestamoService prestamoService,
            HerramientaService herramientaService,
            UsuarioService usuarioService,
            EstadoService estadoService,
            MessageSource messageSource){

        this.prestamoService=prestamoService;
        this.herramientaService=herramientaService;
        this.usuarioService=usuarioService;
        this.estadoService=estadoService;
        this.messageSource=messageSource;

    }

    @GetMapping
    public String index(){
        return "redirect:/admin/prestamos/listado";
    }

    @GetMapping("/listado")
    public String listado(Model model){

        var prestamos=prestamoService.getPrestamos(false);

        model.addAttribute("prestamos",prestamos);
        model.addAttribute("totalPrestamos",prestamos.size());

        return "/admin/prestamos/listado";

    }

    @GetMapping("/nuevo")
    public String nuevo(Model model){

        model.addAttribute("prestamo",new Prestamo());

        cargarCatalogos(model);

        return "/admin/prestamos/modifica";

    }

    @PostMapping("/guardar")
    public String guardar(@Valid Prestamo prestamo,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes){

        if(bindingResult.hasErrors()){
            cargarCatalogos(model);
            return "/admin/prestamos/modifica";
        }

        prestamoService.save(prestamo);

        redirectAttributes.addFlashAttribute("todoOk",msg("prestamo.mensaje.guardado"));

        return "redirect:/admin/prestamos/listado";

    }

    @PostMapping("/eliminar")
    public String eliminar(@RequestParam Integer idPrestamo,
            RedirectAttributes redirectAttributes){

        String titulo="todoOk";
        String detalle=msg("prestamo.mensaje.eliminado");

        try{
            prestamoService.delete(idPrestamo);
        }catch(Exception e){
            titulo="error";
            detalle=msg("prestamo.error.noExiste");
        }

        redirectAttributes.addFlashAttribute(titulo,detalle);

        return "redirect:/admin/prestamos/listado";

    }

    @GetMapping("/modificar/{idPrestamo}")
    public String modificar(@PathVariable Integer idPrestamo,
            Model model,
            RedirectAttributes redirectAttributes){

        var prestamo=prestamoService.getPrestamo(idPrestamo);

        if(prestamo.isEmpty()){

            redirectAttributes.addFlashAttribute("error",msg("prestamo.error.noExiste"));

            return "redirect:/admin/prestamos/listado";

        }

        model.addAttribute("prestamo",prestamo.get());

        cargarCatalogos(model);

        return "/admin/prestamos/modifica";

    }

    private void cargarCatalogos(Model model){

        model.addAttribute("herramientas",herramientaService.getHerramientas(false));
        model.addAttribute("usuarios",usuarioService.getUsuarios(false));
        model.addAttribute("estados",estadoService.getEstados(false));

    }

    private String msg(String key){
        return messageSource.getMessage(key,null,LocaleContextHolder.getLocale());
    }

}