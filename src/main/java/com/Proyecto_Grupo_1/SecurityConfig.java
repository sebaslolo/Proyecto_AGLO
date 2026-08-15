package com.Proyecto_Grupo_1;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
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
                .accessDeniedPage("/acceso_denegado")
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
}
