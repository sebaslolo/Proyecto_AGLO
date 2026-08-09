package com.Proyecto_Grupo_1.controller;

import com.Proyecto_Grupo_1.domain.Retroalimentacion;
import com.Proyecto_Grupo_1.domain.Usuario;
import com.Proyecto_Grupo_1.service.EstadoService;
import com.Proyecto_Grupo_1.service.RetroalimentacionService;
import com.Proyecto_Grupo_1.service.UsuarioService;
import com.Proyecto_Grupo_1.service.VoluntariadoService;
import java.util.Locale;
import org.springframework.context.MessageSource;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/retroalimentacion")
public class RetroalimentacionController {

    private final RetroalimentacionService retroalimentacionService;
    private final UsuarioService usuarioService;
    private final VoluntariadoService voluntariadoService;
    private final EstadoService estadoService;
    private final MessageSource messageSource;

    public RetroalimentacionController(
            RetroalimentacionService retroalimentacionService,
            UsuarioService usuarioService,
            VoluntariadoService voluntariadoService,
            EstadoService estadoService,
            MessageSource messageSource) {

        this.retroalimentacionService = retroalimentacionService;
        this.usuarioService = usuarioService;
        this.voluntariadoService = voluntariadoService;
        this.estadoService = estadoService;
        this.messageSource = messageSource;
    }

    // Listado admin
    @GetMapping("/listado")
    public String listado(Model model) {

        var retroalimentaciones =
                retroalimentacionService
                        .getRetroalimentaciones(false);

        model.addAttribute(
                "retroalimentaciones",
                retroalimentaciones);

        model.addAttribute(
                "totalRetroalimentaciones",
                retroalimentaciones.size());

        return "/retroalimentacion/listado";
    }

    // HU-5 - Formulario para calificar experiencia
    @GetMapping("/calificar/{idVoluntariado}")
    public String calificar(
            @PathVariable Integer idVoluntariado,
            Authentication authentication,
            Model model,
            RedirectAttributes redirectAttributes) {

        Usuario usuario =
                obtenerUsuarioActual(authentication);

        Integer idUsuario =
                usuario.getIdUsuario();

        var voluntariadoOpt =
                voluntariadoService
                        .getVoluntariado(idVoluntariado);

        if (voluntariadoOpt.isEmpty()) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    "El voluntariado no existe.");

            return "redirect:/voluntariados/"
                    + "mis-voluntariados/listado";
        }

        var voluntariado =
                voluntariadoOpt.get();

        // HU-5 - Verificar que el voluntariado
        // pertenezca al usuario
        if (voluntariado.getVoluntariado() == null
                || voluntariado.getVoluntariado()
                        .getUsuario() == null
                || !voluntariado.getVoluntariado()
                        .getUsuario()
                        .getIdUsuario()
                        .equals(idUsuario)) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    "No tiene permiso para calificar "
                    + "este voluntariado.");

            return "redirect:/voluntariados/"
                    + "mis-voluntariados/listado";
        }

        // HU-5 - Verificar que no haya
        // enviado retroalimentación
        if (retroalimentacionService
                .yaEnvioRetroalimentacion(
                        idUsuario,
                        idVoluntariado)) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    msg("retroalimentacion.error.editable"));

            return "redirect:/voluntariados/"
                    + "mis-voluntariados/listado";
        }

        // HU-5 - La retroalimentación
        // se realiza después del voluntariado
        if (!voluntariadoService
                .puedeEnviarRetroalimentacion(
                        idVoluntariado)) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    "La retroalimentación estará disponible "
                    + "después de realizar el voluntariado.");

            return "redirect:/voluntariados/"
                    + "mis-voluntariados/listado";
        }

        model.addAttribute(
                "retroalimentacion",
                new Retroalimentacion());

        model.addAttribute(
                "voluntariado",
                voluntariado);

        return "/retroalimentacion/calificar";
    }

    // HU-5 - Guardar retroalimentación
    @PostMapping("/guardar")
    public String guardar(
            Retroalimentacion retroalimentacion,
            Integer idVoluntariado,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        try {

            Usuario usuario =
                    obtenerUsuarioActual(authentication);

            var voluntariado =
                    voluntariadoService
                            .obtenerVoluntariado(
                                    idVoluntariado);

            // HU-5 - Verificar que el voluntariado
            // pertenezca al usuario
            if (voluntariado.getVoluntariado() == null
                    || voluntariado.getVoluntariado()
                            .getUsuario() == null
                    || !voluntariado.getVoluntariado()
                            .getUsuario()
                            .getIdUsuario()
                            .equals(usuario.getIdUsuario())) {

                throw new IllegalStateException(
                        "No tiene permiso para calificar "
                        + "este voluntariado.");
            }

            // HU-5 - Verificar que la actividad
            // ya se haya realizado
            if (!voluntariadoService
                    .puedeEnviarRetroalimentacion(
                            idVoluntariado)) {

                throw new IllegalStateException(
                        "La retroalimentación estará disponible "
                        + "después de realizar el voluntariado.");
            }

            var estado =
                    estadoService
                            .obtenerEstadoPorNombre(
                                    "Activo");

            retroalimentacion.setUsuario(
                    usuario);

            retroalimentacion.setVoluntariado(
                    voluntariado.getVoluntariado());

            retroalimentacion.setEstado(
                    estado);

            retroalimentacionService
                    .save(retroalimentacion);

            redirectAttributes.addFlashAttribute(
                    "todoOk",
                    msg("retroalimentacion.mensaje.enviada"));

        } catch (IllegalArgumentException
                | IllegalStateException e) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    e.getMessage());
        }

        return "redirect:/voluntariados/"
                + "mis-voluntariados/listado";
    }

    private Usuario obtenerUsuarioActual(
            Authentication authentication) {

        return usuarioService
                .getUsuarioPorUsername(
                        authentication.getName())
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Usuario autenticado "
                                + "no encontrado."));
    }

    private String msg(String key) {

        return messageSource.getMessage(
                key,
                null,
                Locale.getDefault());
    }
}