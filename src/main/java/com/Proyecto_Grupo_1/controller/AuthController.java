package com.Proyecto_Grupo_1.controller;

import com.Proyecto_Grupo_1.domain.Usuario;
import com.Proyecto_Grupo_1.dto.LoginForm;
import com.Proyecto_Grupo_1.dto.RegistroForm;
import com.Proyecto_Grupo_1.service.ActividadService;
import com.Proyecto_Grupo_1.service.EstadoService;
import com.Proyecto_Grupo_1.service.GuiaService;
import com.Proyecto_Grupo_1.service.RolService;
import com.Proyecto_Grupo_1.service.UsuarioRolService;
import com.Proyecto_Grupo_1.service.UsuarioService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    private final ActividadService actividadService;
    private final UsuarioService usuarioService;
    private final UsuarioRolService usuarioRolService;
    private final EstadoService estadoService;
    private final RolService rolService;
    private final GuiaService guiaService;
    private final MessageSource messageSource;

    public AuthController(ActividadService actividadService,
            UsuarioService usuarioService,
            UsuarioRolService usuarioRolService,
            EstadoService estadoService,
            RolService rolService,
            GuiaService guiaService,
            MessageSource messageSource) {
        this.actividadService = actividadService;
        this.usuarioService = usuarioService;
        this.usuarioRolService = usuarioRolService;
        this.estadoService = estadoService;
        this.rolService = rolService;
        this.guiaService = guiaService;
        this.messageSource = messageSource;
    }

    @GetMapping({"/", "/inicio"})
    public String inicio(Model model) {
        model.addAttribute("actividades", actividadService.getActividades(true));
        return "/inicio";
    }

    @GetMapping("/login")
    public String login(Model model) {
        if (!model.containsAttribute("loginForm")) {
            model.addAttribute("loginForm", new LoginForm());
        }
        return "/auth/login";
    }

    @PostMapping("/login")
    public String autenticar(@Valid @ModelAttribute LoginForm loginForm,
            BindingResult bindingResult,
            HttpSession session,
            Model model) {
        if (bindingResult.hasErrors()) {
            return "/auth/login";
        }
        var usuarioOpt = usuarioService.autenticar(loginForm.getCorreo(), loginForm.getPassword());
        if (usuarioOpt.isEmpty()) {
            model.addAttribute("error", msg("error.login"));
            return "/auth/login";
        }
        Usuario usuario = usuarioOpt.get();
        if (usuario.getEstado() != null
                && usuario.getEstado().getNombreEstado() != null
                && !"activo".equalsIgnoreCase(usuario.getEstado().getNombreEstado())) {
            model.addAttribute("error", msg("login.error.inactivo"));
            return "/auth/login";
        }

        List<String> roles = usuarioRolService.getRolesPorUsuario(usuario.getIdUsuario()).stream()
                .map(usuarioRol -> usuarioRol.getRol().getRol())
                .toList();
        session.setAttribute("idUsuario", usuario.getIdUsuario());
        session.setAttribute("nombreUsuario", usuario.getNombre());
        session.setAttribute("rolesUsuario", roles);
        guiaService.getGuiaPorUsuario(usuario.getIdUsuario())
                .ifPresent(guia -> session.setAttribute("idGuia", guia.getIdGuia()));

        if (tieneRol(roles, "ADMIN")) {
            return "redirect:/admin/dashboard";
        }
        if (tieneRol(roles, "GUIA") || tieneRol(roles, "GUÍA")) {
            return "redirect:/guia/agenda";
        }
        return "redirect:/catalogo/listado";
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
            var estadoActivo = estadoService.obtenerEstadoPorNombre("Activo");
            var usuario = usuarioService.registrarCliente(registroForm, estadoActivo);
            rolService.getRolPorNombre("CLIENTE")
                    .ifPresent(rol -> usuarioRolService.reemplazarRolPrincipal(usuario.getIdUsuario(), rol.getIdRol()));
            redirectAttributes.addFlashAttribute("todoOk", msg("registro.mensaje.exitoso"));
            return "redirect:/login";
        } catch (IllegalArgumentException | IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            redirectAttributes.addFlashAttribute("registroForm", registroForm);
            return "redirect:/registro";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes redirectAttributes) {
        session.invalidate();
        redirectAttributes.addFlashAttribute("todoOk", "Sesión cerrada correctamente.");
        return "redirect:/login";
    }

    @GetMapping("/forgot-password")
    public String forgotPassword() {
        return "/auth/forgot-password";
    }

    private boolean tieneRol(List<String> roles, String esperado) {
        return roles.stream()
                .map(String::toUpperCase)
                .anyMatch(rol -> rol.contains(esperado));
    }

    private String msg(String key) {
        return messageSource.getMessage(key, null, LocaleContextHolder.getLocale());
    }
}
