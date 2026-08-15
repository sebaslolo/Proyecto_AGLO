package com.Proyecto_Grupo_1.controller;

import com.Proyecto_Grupo_1.dto.LoginForm;
import com.Proyecto_Grupo_1.dto.RegistroForm;
import com.Proyecto_Grupo_1.service.ActividadService;
import com.Proyecto_Grupo_1.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    private final ActividadService actividadService;
    private final UsuarioService usuarioService;
    private final MessageSource messageSource;

    public AuthController(ActividadService actividadService,
            UsuarioService usuarioService,
            MessageSource messageSource) {
        this.actividadService = actividadService;
        this.usuarioService = usuarioService;
        this.messageSource = messageSource;
    }

    @GetMapping({"/", "/inicio"})
    public String inicio(Model model) {
        model.addAttribute("actividades", actividadService.getActividades(true));
        return "/inicio";
    }

    @GetMapping("/login")
    public String login(@RequestParam(required = false) String error,
            @RequestParam(required = false) String logout,
            @RequestParam(required = false) String expired,
            Model model) {
        if (!model.containsAttribute("loginForm")) {
            model.addAttribute("loginForm", new LoginForm());
        }
        if (error != null) {
            model.addAttribute("error", msg("error.login"));
        }
        if (logout != null) {
            model.addAttribute("todoOk", "Sesión cerrada correctamente.");
        }
        if (expired != null) {
            model.addAttribute("error", "La sesión expiró. Inicie sesión nuevamente.");
        }
        return "/auth/login";
    }

    @GetMapping("/registro")
    public String registro(Model model) {
        if (!model.containsAttribute("registroForm")) {
            model.addAttribute("registroForm", new RegistroForm());
        }
        return "/auth/registro";
    }

    @PostMapping("/registro")
    public String registrar(@Valid @ModelAttribute RegistroForm registroForm,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {
        if (registroForm.getPassword() == null || !registroForm.getPassword().equals(registroForm.getConfirmar())) {
            bindingResult.rejectValue("confirmar", "registro.error.passwordConfirmacion", msg("registro.error.passwordConfirmacion"));
        }
        if (usuarioService.existeCorreo(registroForm.getCorreo())) {
            bindingResult.rejectValue("correo", "registro.error.correoDuplicado", msg("registro.error.correoDuplicado"));
        }
        if (usuarioService.existeUsername(registroForm.getUsername())) {
            bindingResult.rejectValue("username", "registro.error.usernameDuplicado", msg("registro.error.usernameDuplicado"));
        }
        if (bindingResult.hasErrors()) {
            return "/auth/registro";
        }
        try {
            usuarioService.registrarCliente(registroForm);
            redirectAttributes.addFlashAttribute("todoOk", msg("registro.mensaje.exitoso"));
            return "redirect:/login";
        } catch (IllegalArgumentException | IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            redirectAttributes.addFlashAttribute("registroForm", registroForm);
            return "redirect:/registro";
        }
    }


    @GetMapping("/forgot-password")
    public String forgotPassword() {
        return "/auth/forgot-password";
    }

    @GetMapping("/acceso_denegado")
    public String accesoDenegado() {
        return "/acceso_denegado";
    }

    private String msg(String key) {
        return messageSource.getMessage(key, null, LocaleContextHolder.getLocale());
    }
}
