package com.Proyecto_Grupo_1.controller;

import com.Proyecto_Grupo_1.domain.Usuario;
import com.Proyecto_Grupo_1.dto.LoginForm;
import com.Proyecto_Grupo_1.dto.RegistroForm;
import com.Proyecto_Grupo_1.service.ActividadService;
import com.Proyecto_Grupo_1.service.CorreoService;
import com.Proyecto_Grupo_1.service.UsuarioService;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import java.security.SecureRandom;
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
    private final CorreoService correoService;

    public AuthController(ActividadService actividadService,
            UsuarioService usuarioService,
            MessageSource messageSource,
            CorreoService correoService) {
        this.actividadService = actividadService;
        this.usuarioService = usuarioService;
        this.messageSource = messageSource;
        this.correoService = correoService;
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
        if (registroForm.getPassword() == null
                || !registroForm.getPassword().equals(registroForm.getConfirmar())) {
            bindingResult.rejectValue(
                    "confirmar",
                    "registro.error.passwordConfirmacion",
                    msg("registro.error.passwordConfirmacion"));
        }

        if (usuarioService.existeCorreo(registroForm.getCorreo())) {
            bindingResult.rejectValue(
                    "correo",
                    "registro.error.correoDuplicado",
                    msg("registro.error.correoDuplicado"));
        }

        if (usuarioService.existeUsername(registroForm.getUsername())) {
            bindingResult.rejectValue(
                    "username",
                    "registro.error.usernameDuplicado",
                    msg("registro.error.usernameDuplicado"));
        }

        if (bindingResult.hasErrors()) {
            return "/auth/registro";
        }

        try {
            usuarioService.registrarCliente(registroForm);

            redirectAttributes.addFlashAttribute(
                    "todoOk",
                    msg("registro.mensaje.exitoso"));

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

    @PostMapping("/forgot-password")
    public String recuperarPassword(
            @RequestParam String correo,
            RedirectAttributes redirectAttributes) {

        try {
            Usuario usuario = usuarioService.getUsuarioPorCorreo(correo)
                    .orElseThrow(() -> new IllegalArgumentException(
                    "No existe un usuario registrado con ese correo."));

            String passwordTemporal = generarPasswordTemporal();

            String asunto = "Recuperación de contraseña - AGLO";

            String contenido
                    = "<html>"
                    + "<body style='font-family:Arial,sans-serif;background:#f4f4f4;padding:30px;'>"
                    + "<div style='max-width:600px;margin:auto;background:white;"
                    + "border-radius:10px;overflow:hidden;'>"
                    + "<div style='background:#198754;padding:20px;text-align:center;'>"
                    + "<h1 style='color:white;margin:0;'>AGLO</h1>"
                    + "<p style='color:white;margin-top:8px;'>Recuperación de contraseña</p>"
                    + "</div>"
                    + "<div style='padding:30px;'>"
                    + "<h2 style='color:#198754;'>¡Hola "
                    + usuario.getNombre()
                    + "!</h2>"
                    + "<p>Se solicitó recuperar el acceso a su cuenta de AGLO.</p>"
                    + "<p>Su nueva contraseña es:</p>"
                    + "<div style='font-size:22px;font-weight:bold;"
                    + "background:#f1f1f1;padding:15px;text-align:center;"
                    + "border-radius:8px;'>"
                    + passwordTemporal
                    + "</div>"
                    + "<p style='margin-top:20px;'>"
                    + "Utilice esta contraseña para iniciar sesión."
                    + "</p>"
                    + "<p>Por seguridad, recomendamos cambiarla posteriormente con el administrador.</p>"
                    + "</div>"
                    + "<div style='background:#198754;color:white;"
                    + "padding:15px;text-align:center;'>"
                    + "AGLO © 2026"
                    + "</div>"
                    + "</div>"
                    + "</body>"
                    + "</html>";

            correoService.enviarCorreoHtml(
                    usuario.getCorreo(),
                    asunto,
                    contenido);

            usuarioService.cambiarPasswordTemporal(
                    usuario.getCorreo(),
                    passwordTemporal);

            redirectAttributes.addFlashAttribute(
                    "todoOk",
                    "Se envió una contraseña temporal al correo registrado.");

            return "redirect:/login";

        } catch (IllegalArgumentException e) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    e.getMessage());

            return "redirect:/forgot-password";

        } catch (MessagingException e) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    "No se pudo enviar el correo de recuperación.");

            return "redirect:/forgot-password";
        }
    }

    @GetMapping("/acceso_denegado")
    public String accesoDenegado() {
        return "/acceso_denegado";
    }

    private String generarPasswordTemporal() {

        String caracteres
                = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz23456789";

        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder();

        for (int i = 0; i < 9; i++) {
            password.append(
                    caracteres.charAt(
                            random.nextInt(caracteres.length())));
        }

        password.append(random.nextInt(10));

        return password.toString();
    }

    private String msg(String key) {
        return messageSource.getMessage(
                key,
                null,
                LocaleContextHolder.getLocale());
    }
}