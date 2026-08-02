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

@Controller
@RequestMapping("/voluntariados")
public class VoluntariadoController {

    private final ActividadService actividadService;
    private final VoluntariadoService voluntariadoService;

    public VoluntariadoController(
            ActividadService actividadService,
            VoluntariadoService voluntariadoService
    ) {
        this.actividadService = actividadService;
        this.voluntariadoService = voluntariadoService;
    }

    @GetMapping
    public String index() {
        return "redirect:/voluntariados/listado";
    }

    @GetMapping("/listado")
    public String listado(Model model, HttpSession session) {
        var voluntariados = actividadService.listarVoluntariadosFuturos();
        Integer idUsuario = (Integer) session.getAttribute("idUsuario");
        Map<Integer, Long> cuposDisponibles = new HashMap<>();
        Map<Integer, Boolean> inscripcionesUsuario = new HashMap<>();
        for (var actividad : voluntariados) {
            Integer idActividad = actividad.getIdActividad();
            cuposDisponibles.put(
                    idActividad,
                    voluntariadoService.obtenerCuposDisponibles(idActividad)
            );
            boolean inscrito = idUsuario != null
                    && voluntariadoService.usuarioYaInscrito(idUsuario, idActividad);
            inscripcionesUsuario.put(idActividad, inscrito);
        }
        if (!model.containsAttribute("voluntariadoForm")) {
            model.addAttribute("voluntariadoForm", new VoluntariadoForm());
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
            HttpSession session,
            RedirectAttributes redirectAttributes
    ) {
        Integer idUsuario = (Integer) session.getAttribute("idUsuario");
        if (idUsuario == null) {
            redirectAttributes.addFlashAttribute(
                    "error",
                    "Debe iniciar sesión para inscribirse."
            );
            return "redirect:/auth/login";
        }
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute(
                    "error",
                    "Revise la información ingresada."
            );
            return "redirect:/voluntariados/listado";
        }
        try {
            voluntariadoService.inscribir(
                    idUsuario,
                    voluntariadoForm.getIdActividad(),
                    voluntariadoForm.getHerramientasUtilizadas()
            );
            redirectAttributes.addFlashAttribute(
                    "todoOk",
                    "Inscripción realizada correctamente. Tu participación ha sido confirmada."
            );
        } catch (IllegalArgumentException | IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/voluntariados/listado";
    }

    @GetMapping("/mis-inscripciones")
    public String misInscripciones(
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        Integer idUsuario = (Integer) session.getAttribute("idUsuario");
        if (idUsuario == null) {
            redirectAttributes.addFlashAttribute("error", "Debe iniciar sesión.");
            return "redirect:/auth/login";
        }
        var inscripciones = voluntariadoService.listarPorUsuario(idUsuario);
        model.addAttribute("inscripciones", inscripciones);
        return "/voluntariados/mis-inscripciones";
    }
}
