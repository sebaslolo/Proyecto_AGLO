package com.Proyecto_Grupo_1;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.security.autoconfigure.web.servlet.ServletWebSecurityAutoConfiguration;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@WebMvcTest(controllers = SecurityConfigMvcTest.ProtectedRoutesController.class)
@Import({SecurityConfig.class, SecurityConfigMvcTest.TestUsersConfiguration.class})
@ImportAutoConfiguration(ServletWebSecurityAutoConfiguration.class)
class SecurityConfigMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private FilterChainProxy springSecurityFilterChain;

    @Test
    void redirigeUsuariosAnonimosAlLogin() throws Exception {
        mockMvc.perform(get("/admin/dashboard"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    void aplicaLasReglasEstaticasDeCadaRol() throws Exception {
        mockMvc.perform(get("/admin/dashboard").with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk());
        mockMvc.perform(get("/admin/dashboard").with(user("cliente").roles("CLIENTE")))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/guia/agenda").with(user("guia").roles("GUIA")))
                .andExpect(status().isOk());
        mockMvc.perform(get("/guia/agenda").with(user("cliente").roles("CLIENTE")))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/reservaciones/confirmacion/7").with(user("cliente").roles("CLIENTE")))
                .andExpect(status().isOk());
        mockMvc.perform(get("/reservaciones/confirmacion/7").with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk());
        mockMvc.perform(get("/reservaciones/confirmacion/7").with(user("guia").roles("GUIA")))
                .andExpect(status().isForbidden());
    }

    @Test
    void rechazaCsrfInvalidoEnUnaSesionVigente() throws Exception {
        CsrfFilter csrfFilter = springSecurityFilterChain.getFilters("/admin/operacion").stream()
                .filter(CsrfFilter.class::isInstance)
                .map(CsrfFilter.class::cast)
                .findFirst()
                .orElseThrow();

        MockHttpSession session = new MockHttpSession();
        MockHttpServletRequest getRequest = new MockHttpServletRequest("GET", "/admin/operacion");
        getRequest.setSession(session);
        csrfFilter.doFilter(getRequest, new MockHttpServletResponse(), new MockFilterChain());
        CsrfToken token = (CsrfToken) getRequest.getAttribute(CsrfToken.class.getName());
        assertThat(token).isNotNull();
        token.getToken();

        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/admin/operacion");
        request.setSession(session);
        request.setParameter(token.getParameterName(), "token-invalido");
        MockHttpServletResponse response = new MockHttpServletResponse();

        csrfFilter.doFilter(request, response, new MockFilterChain());

        assertThat(response.getStatus()).isEqualTo(403);
    }

    @Test
    void autenticaConParametrosExplicitosYUsaRedireccionPorRol() throws Exception {
        mockMvc.perform(post("/login")
                .with(csrf())
                .param("username", "cliente")
                .param("password", "Clave123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/catalogo/listado"));

        mockMvc.perform(post("/login")
                .with(csrf())
                .param("username", "cliente")
                .param("password", "incorrecta1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?error=true"));
    }

    @Test
    void deniegaRutasNoClasificadasInclusoSiHaySesion() throws Exception {
        mockMvc.perform(get("/ruta-no-clasificada").with(user("cliente").roles("CLIENTE")))
                .andExpect(status().isForbidden());
    }

    @TestConfiguration(proxyBeanMethods = false)
    static class TestUsersConfiguration {

        @Bean
        UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
            return username -> switch (username) {
                case "admin" -> User.withUsername("admin")
                        .password(passwordEncoder.encode("Clave123"))
                        .roles("ADMIN")
                        .build();
                case "guia" -> User.withUsername("guia")
                        .password(passwordEncoder.encode("Clave123"))
                        .roles("GUIA")
                        .build();
                case "cliente" -> User.withUsername("cliente")
                        .password(passwordEncoder.encode("Clave123"))
                        .roles("CLIENTE")
                        .build();
                default -> throw new UsernameNotFoundException(username);
            };
        }

        @Bean
        ProtectedRoutesController protectedRoutesController() {
            return new ProtectedRoutesController();
        }
    }

    @Controller
    public static class ProtectedRoutesController {

        @GetMapping({"/admin/dashboard", "/guia/agenda", "/reservaciones/confirmacion/{id}"})
        @ResponseBody
        String consulta() {
            return "ok";
        }

        @PostMapping("/admin/operacion")
        @ResponseBody
        String operacion() {
            return "ok";
        }

        @GetMapping("/acceso_denegado")
        @ResponseBody
        String accesoDenegado() {
            return "denegado";
        }
    }
}
