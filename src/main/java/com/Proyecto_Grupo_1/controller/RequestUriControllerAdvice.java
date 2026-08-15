package com.Proyecto_Grupo_1.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * Supplies the application-relative URI to MVC views. Thymeleaf 3.1 no longer
 * exposes servlet request objects directly, and the admin navigation uses this
 * value to mark the active section and retain the current page for locale links.
 */
@ControllerAdvice
public class RequestUriControllerAdvice {

    @ModelAttribute("requestUri")
    public String requestUri(HttpServletRequest request) {
        String uri = request.getRequestURI();
        String contextPath = request.getContextPath();
        if (contextPath != null && !contextPath.isEmpty() && uri.startsWith(contextPath)) {
            return uri.substring(contextPath.length());
        }
        return uri;
    }
}
