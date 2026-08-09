package com.Proyecto_Grupo_1.controller;

import com.Proyecto_Grupo_1.dto.VoluntariadoForm;
import com.Proyecto_Grupo_1.service.ActividadService;
import com.Proyecto_Grupo_1.service.VoluntariadoService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.Proyecto_Grupo_1.domain.Usuario;
import com.Proyecto_Grupo_1.service.UsuarioService;
import org.springframework.security.core.Authentication;

@Controller
@RequestMapping("/voluntariados")
public class VoluntariadoController {

    private final ActividadService actividadService;
    private final VoluntariadoService voluntariadoService;
    private final UsuarioService usuarioService;

    public VoluntariadoController(
            ActividadService actividadService,
            VoluntariadoService voluntariadoService,
            UsuarioService usuarioService) {

        this.actividadService = actividadService;
        this.voluntariadoService = voluntariadoService;
        this.usuarioService = usuarioService;
    }
    @GetMapping
    public String index() {
        return "redirect:/voluntariados/listado";
    }

    @GetMapping("/listado")
    public String listado(Model model, HttpSession sesion) {

        var voluntariados = actividadService.listarActividadesFuturas();

        Integer idUsuario = (Integer) sesion.getAttribute("idUsuario");

        Map<Integer, Long> cuposDisponibles = new HashMap<>();
        Map<Integer, Boolean> inscripcionesUsuario = new HashMap<>();

        for (var actividad : voluntariados) {

            Integer idActividad = actividad.getIdActividad();

            cuposDisponibles.put(
                    idActividad,
                    voluntariadoService.obtenerCuposDisponibles(idActividad)
            );

            boolean inscrito = idUsuario != null
                    && voluntariadoService.usuarioYaInscrito(
                            idUsuario,
                            idActividad);

            inscripcionesUsuario.put(idActividad, inscrito);
        }

        if (!model.containsAttribute("voluntariadoForm")) {
            model.addAttribute(
                    "voluntariadoForm",
                    new VoluntariadoForm());
        }

        model.addAttribute("voluntariados", voluntariados);
        model.addAttribute("cuposDisponibles", cuposDisponibles);
        model.addAttribute("inscripcionesUsuario", inscripcionesUsuario);

        return "/voluntariados/listado";
    }

    @PostMapping("/inscribir")
    public String inscribir(
            @Valid VoluntariadoForm voluntariadoForm,
            BindingResult bindingResult,
            HttpSession sesion,
            RedirectAttributes redirectAttributes) {

        Integer idUsuario =
                (Integer) sesion.getAttribute("idUsuario");

        if (idUsuario == null) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    "Debe iniciar sesión para inscribirse.");

            return "redirect:/auth/login";
        }

        if (bindingResult.hasErrors()) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    "Revise la información ingresada.");

            return "redirect:/voluntariados/listado";
        }

        try {

            voluntariadoService.inscribir(
                    idUsuario,
                    voluntariadoForm.getIdActividad()
            );

            redirectAttributes.addFlashAttribute(
                    "todoOk",
                    "Inscripción realizada correctamente. "
                    + "Tu participación ha sido confirmada."
            );

        } catch (IllegalArgumentException | IllegalStateException e) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    e.getMessage());
        }

        return "redirect:/voluntariados/listado";
    }

    @GetMapping("/mis-inscripciones")
    public String misInscripciones(
            HttpSession sesion,
            Model model,
            RedirectAttributes redirectAttributes) {

        Integer idUsuario =
                (Integer) sesion.getAttribute("idUsuario");

        if (idUsuario == null) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    "Debe iniciar sesión.");

            return "redirect:/auth/login";
        }

        var inscripciones =
                voluntariadoService.listarPorUsuario(idUsuario);

        model.addAttribute(
                "inscripciones",
                inscripciones);

        return "/voluntariados/mis-inscripciones";
    }

    // HU-5 - Mis voluntariados para retroalimentación
    @GetMapping("/mis-voluntariados")
    public String indexMisVoluntariados() {

        return "redirect:/voluntariados/mis-voluntariados/listado";
    }

    // HU-5 - Historial de voluntariados del usuario
 @GetMapping("/mis-voluntariados/listado")
    public String misVoluntariados(
            Authentication authentication,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (authentication == null
                || !authentication.isAuthenticated()) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    "Debe iniciar sesión.");

            return "redirect:/login";
        }

        Usuario usuario
                = usuarioService
                        .getUsuarioPorUsername(
                                authentication.getName())
                        .orElseThrow(()
                                -> new IllegalStateException(
                                "Usuario autenticado no encontrado."));

        Integer idUsuario
                = usuario.getIdUsuario();

        var voluntariados
                = voluntariadoService
                        .getVoluntariadosPorUsuario(
                                idUsuario);

        model.addAttribute(
                "voluntariados",
                voluntariados);

        model.addAttribute(
                "totalVoluntariados",
                voluntariados.size());

        return "/voluntariados/mis-voluntariados";
    }
}