package com.Proyecto_Grupo_1;

import com.Proyecto_Grupo_1.domain.Usuario;
import com.Proyecto_Grupo_1.service.GuiaService;
import com.Proyecto_Grupo_1.service.UsuarioRolService;
import com.Proyecto_Grupo_1.service.UsuarioService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.text.Normalizer;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
            AuthenticationSuccessHandler authenticationSuccessHandler) throws Exception {
        http.authorizeHttpRequests(request -> request
                .requestMatchers(
                        "/", "/inicio", "/login", "/registro", "/forgot-password",
                        "/error", "/favicon.ico", "/css/**", "/js/**", "/img/**", "/webjars/**")
                .permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/guia/**").hasAnyRole("GUIA", "ADMIN")
                .requestMatchers("/catalogo/**", "/reservaciones/**", "/mis-reservaciones/**").authenticated()
                .anyRequest().authenticated()
        ).formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .usernameParameter("correo")
                .passwordParameter("password")
                .successHandler(authenticationSuccessHandler)
                .failureUrl("/login?error=true")
                .permitAll()
        ).logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout=true")
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
        ).sessionManagement(session -> session
                .maximumSessions(1)
                .maxSessionsPreventsLogin(false)
        );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(UsuarioService usuarioService,
            UsuarioRolService usuarioRolService) {
        return identificador -> {
            Usuario usuario = usuarioService.getUsuarioPorIdentificador(identificador)
                    .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
            List<SimpleGrantedAuthority> authorities = usuarioRolService.getRolesPorUsuario(usuario.getIdUsuario()).stream()
                    .map(usuarioRol -> usuarioRol.getRol().getRol())
                    .map(SecurityConfig::normalizarRol)
                    .map(rol -> new SimpleGrantedAuthority("ROLE_" + rol))
                    .toList();

            return User.withUsername(usuario.getUsername())
                    .password(usuario.getPassword())
                    .authorities(authorities)
                    .disabled(!estaActivo(usuario))
                    .build();
        };
    }

    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler(UsuarioService usuarioService,
            UsuarioRolService usuarioRolService,
            GuiaService guiaService) {
        return (request, response, authentication) -> manejarAutenticacionExitosa(
                request, response, authentication, usuarioService, usuarioRolService, guiaService);
    }

    private static void manejarAutenticacionExitosa(HttpServletRequest request,
            HttpServletResponse response,
            org.springframework.security.core.Authentication authentication,
            UsuarioService usuarioService,
            UsuarioRolService usuarioRolService,
            GuiaService guiaService) throws IOException {
        Usuario usuario = usuarioService.getUsuarioPorIdentificador(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
        List<String> roles = usuarioRolService.getRolesPorUsuario(usuario.getIdUsuario()).stream()
                .map(usuarioRol -> usuarioRol.getRol().getRol())
                .toList();
        HttpSession session = request.getSession();
        session.setAttribute("idUsuario", usuario.getIdUsuario());
        session.setAttribute("nombreUsuario", usuario.getNombre());
        session.setAttribute("rolesUsuario", roles);
        guiaService.getGuiaPorUsuario(usuario.getIdUsuario())
                .ifPresent(guia -> session.setAttribute("idGuia", guia.getIdGuia()));

        if (tieneRol(roles, "ADMIN")) {
            response.sendRedirect(request.getContextPath() + "/admin/dashboard");
        } else if (tieneRol(roles, "GUIA")) {
            response.sendRedirect(request.getContextPath() + "/guia/agenda");
        } else {
            response.sendRedirect(request.getContextPath() + "/catalogo/listado");
        }
    }

    private static boolean estaActivo(Usuario usuario) {
        return usuario.getEstado() != null
                && "ACTIVO".equals(normalizar(usuario.getEstado().getNombreEstado()));
    }

    private static boolean tieneRol(List<String> roles, String esperado) {
        return roles.stream().map(SecurityConfig::normalizarRol).anyMatch(esperado::equals);
    }

    private static String normalizarRol(String rol) {
        return normalizar(rol).replaceAll("\\s+", "_");
    }

    private static String normalizar(String valor) {
        if (valor == null) {
            return "";
        }
        return Normalizer.normalize(valor, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .trim()
                .toUpperCase();
    }
}
