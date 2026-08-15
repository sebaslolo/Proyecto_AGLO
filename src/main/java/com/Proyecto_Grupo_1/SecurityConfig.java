package com.Proyecto_Grupo_1;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.access.AccessDeniedHandlerImpl;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.csrf.CsrfException;
import org.springframework.security.web.session.HttpSessionEventPublisher;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
            DaoAuthenticationProvider authenticationProvider,
            AuthenticationSuccessHandler authenticationSuccessHandler,
            SessionRegistry sessionRegistry)
            throws Exception {
        http.authenticationProvider(authenticationProvider);
        http.csrf(Customizer.withDefaults())
        .authorizeHttpRequests(requests -> requests
                // Static assets and explicitly public MVC endpoints.
                .requestMatchers("/css/**", "/js/**", "/img/**", "/webjars/**", "/favicon.ico").permitAll()
                .requestMatchers(
                        "/", "/inicio", "/login", "/registro", "/forgot-password",
                        "/acceso_denegado", "/error",
                        "/catalogo/**", "/retroalimentacion/**", "/avistamientos/**",
                        "/herramientas/**", "/voluntariados/**")
                .permitAll()
                // Reservation data is restricted to clients; the controller additionally
                // verifies ownership for the confirmation view.
                .requestMatchers("/reservaciones/confirmacion/**").hasAnyRole("CLIENTE", "ADMIN")
                .requestMatchers("/reservaciones/**", "/mis-reservaciones/**").hasRole("CLIENTE")
                // Guides may operate only on the explicitly listed administrative
                // resources.  Keep these matchers before the ADMIN-only /admin/**
                // fallback so unrelated administration remains closed to guides.
                .requestMatchers(HttpMethod.POST,
                        "/admin/herramientas/eliminar",
                        "/admin/prestamos/eliminar",
                        "/admin/tortugas/eliminar",
                        "/admin/avistamientos/eliminar",
                        "/admin/nidos/eliminar",
                        "/admin/nacimientos/eliminar",
                        "/admin/monitoreos/eliminar")
                .hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST,
                        "/admin/herramientas/guardar",
                        "/admin/prestamos/guardar",
                        "/admin/tortugas/guardar",
                        "/admin/avistamientos/guardar",
                        "/admin/nidos/guardar",
                        "/admin/nacimientos/guardar",
                        "/admin/monitoreos/guardar")
                .hasAnyRole("ADMIN", "GUIA")
                .requestMatchers(HttpMethod.GET,
                        "/admin/herramientas", "/admin/herramientas/listado",
                        "/admin/herramientas/nuevo", "/admin/herramientas/modificar/**",
                        "/admin/prestamos", "/admin/prestamos/listado",
                        "/admin/prestamos/nuevo", "/admin/prestamos/modificar/**",
                        "/admin/tortugas", "/admin/tortugas/listado",
                        "/admin/tortugas/nuevo", "/admin/tortugas/modificar/**",
                        "/admin/avistamientos", "/admin/avistamientos/listado",
                        "/admin/avistamientos/nuevo", "/admin/avistamientos/modificar/**",
                        "/admin/nidos", "/admin/nidos/listado",
                        "/admin/nidos/nuevo", "/admin/nidos/modificar/**",
                        "/admin/nacimientos", "/admin/nacimientos/listado",
                        "/admin/nacimientos/nuevo", "/admin/nacimientos/modificar/**",
                        "/admin/monitoreos", "/admin/monitoreos/listado",
                        "/admin/monitoreos/nuevo", "/admin/monitoreos/modificar/**")
                .hasAnyRole("ADMIN", "GUIA")
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/guia/**").hasRole("GUIA")
                // New endpoints stay closed until they are deliberately classified above.
                .anyRequest().denyAll()
        );
        http.formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .usernameParameter("username")
                .passwordParameter("password")
                .successHandler(authenticationSuccessHandler)
                .failureUrl("/login?error=true")
                .permitAll()
        ).logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout=true")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
        ).exceptionHandling(exceptions -> exceptions
                .accessDeniedHandler(csrfAwareAccessDeniedHandler())
        ).sessionManagement(session -> session
                .invalidSessionUrl("/login?expired=true")
                .maximumSessions(1)
                .maxSessionsPreventsLogin(false)
                .sessionRegistry(sessionRegistry)
                .expiredUrl("/login?expired=true")
        );
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider(
            UserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    /**
     * Publishes servlet session lifecycle events so Spring Security can enforce
     * concurrent-session limits and expired-session redirects.
     */
    @Bean
    public HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }

    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }

    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
        return (request, response, authentication) -> {
            String destino;
            if (tieneRol(authentication, "ADMIN")) {
                destino = "/admin/dashboard";
            } else if (tieneRol(authentication, "GUIA")) {
                destino = "/guia/agenda";
            } else {
                destino = "/catalogo/listado";
            }
            response.sendRedirect(request.getContextPath() + destino);
        };
    }

    private static boolean tieneRol(
            org.springframework.security.core.Authentication authentication,
            String rol) {
        return authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_" + rol));
    }

    /**
     * Keeps the existing access-denied page for authorization failures while
     * returning the conventional 403 response for invalid CSRF submissions.
     */
    private static AccessDeniedHandler csrfAwareAccessDeniedHandler() {
        AccessDeniedHandlerImpl accessDeniedPageHandler = new AccessDeniedHandlerImpl();
        accessDeniedPageHandler.setErrorPage("/acceso_denegado");
        return (request, response, exception) -> {
            if (exception instanceof CsrfException) {
                response.sendError(HttpStatus.FORBIDDEN.value());
                return;
            }
            accessDeniedPageHandler.handle(request, response, exception);
        };
    }
}
