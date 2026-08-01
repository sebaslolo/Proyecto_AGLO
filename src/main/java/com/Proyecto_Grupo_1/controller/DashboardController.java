package com.Proyecto_Grupo_1.controller;

import com.Proyecto_Grupo_1.domain.UsuarioRol;
import com.Proyecto_Grupo_1.service.ActividadService;
import com.Proyecto_Grupo_1.service.GuiaService;
import com.Proyecto_Grupo_1.service.ReservacionService;
import com.Proyecto_Grupo_1.service.UsuarioRolService;
import com.Proyecto_Grupo_1.service.UsuarioService;
import java.util.stream.Collectors;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    private final UsuarioService usuarioService;
    private final ActividadService actividadService;
    private final ReservacionService reservacionService;
    private final GuiaService guiaService;
    private final UsuarioRolService usuarioRolService;

    public DashboardController(UsuarioService usuarioService,
            ActividadService actividadService,
            ReservacionService reservacionService,
            GuiaService guiaService,
            UsuarioRolService usuarioRolService) {
        this.usuarioService = usuarioService;
        this.actividadService = actividadService;
        this.reservacionService = reservacionService;
        this.guiaService = guiaService;
        this.usuarioRolService = usuarioRolService;
    }

    @GetMapping("/admin/dashboard")
    public String dashboard(Model model) {
        var usuarios = usuarioService.getUsuarios(false);
        var actividades = actividadService.getActividades(false);
        var reservaciones = reservacionService.getReservaciones(false);
        var guias = guiaService.getGuias(false);
        var rolesPorUsuario = usuarios.stream()
                .collect(Collectors.toMap(
                        usuario -> usuario.getIdUsuario(),
                        usuario -> usuarioRolService.getRolesPorUsuario(usuario.getIdUsuario()).stream()
                                .map(UsuarioRol::getRol)
                                .map(rol -> rol.getRol())
                                .collect(Collectors.joining(", "))));

        model.addAttribute("dashboardTitle", "Registro de nuevo usuario");
        model.addAttribute("activeSection", "dashboard");
        model.addAttribute("usuarios", usuarios);
        model.addAttribute("totalUsuarios", usuarios.size());
        model.addAttribute("actividades", actividades);
        model.addAttribute("totalActividades", actividades.size());
        model.addAttribute("reservaciones", reservaciones);
        model.addAttribute("totalReservaciones", reservaciones.size());
        model.addAttribute("guias", guias);
        model.addAttribute("totalGuias", guias.size());
        model.addAttribute("rolesPorUsuario", rolesPorUsuario);
        return "/admin/dashboard";
    }
}
