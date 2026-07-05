package com.Proyecto_Grupo_1.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class SesionInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        String uri = request.getRequestURI();
        var session = request.getSession(false);
        Integer idUsuario = session == null ? null : (Integer) session.getAttribute("idUsuario");
        if (idUsuario == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return false;
        }

        if (uri.startsWith(request.getContextPath() + "/admin") && !tieneRol(session.getAttribute("rolesUsuario"), "ADMIN")) {
            response.sendRedirect(request.getContextPath() + "/catalogo/listado");
            return false;
        }

        if (uri.startsWith(request.getContextPath() + "/guia")
                && !tieneRol(session.getAttribute("rolesUsuario"), "GUIA")
                && !tieneRol(session.getAttribute("rolesUsuario"), "GUÍA")
                && !tieneRol(session.getAttribute("rolesUsuario"), "ADMIN")) {
            response.sendRedirect(request.getContextPath() + "/catalogo/listado");
            return false;
        }

        return true;
    }

    private boolean tieneRol(Object roles, String esperado) {
        if (roles instanceof Collection<?> coleccion) {
            return coleccion.stream()
                    .map(String::valueOf)
                    .map(String::toUpperCase)
                    .anyMatch(rol -> rol.contains(esperado));
        }
        return false;
    }
}
