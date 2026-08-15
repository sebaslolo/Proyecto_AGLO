package com.Proyecto_Grupo_1.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import com.Proyecto_Grupo_1.domain.Avistamiento;
import com.Proyecto_Grupo_1.domain.Estado;
import com.Proyecto_Grupo_1.domain.Guia;
import com.Proyecto_Grupo_1.domain.Herramienta;
import com.Proyecto_Grupo_1.domain.Monitoreo;
import com.Proyecto_Grupo_1.domain.Nacimiento;
import com.Proyecto_Grupo_1.domain.Nido;
import com.Proyecto_Grupo_1.domain.Prestamo;
import com.Proyecto_Grupo_1.domain.Tortuga;
import com.Proyecto_Grupo_1.domain.Usuario;
import com.Proyecto_Grupo_1.service.AvistamientoService;
import com.Proyecto_Grupo_1.service.EstadoService;
import com.Proyecto_Grupo_1.service.GuiaService;
import com.Proyecto_Grupo_1.service.HerramientaService;
import com.Proyecto_Grupo_1.service.MonitoreoService;
import com.Proyecto_Grupo_1.service.NacimientoService;
import com.Proyecto_Grupo_1.service.NidoService;
import com.Proyecto_Grupo_1.service.PrestamoService;
import com.Proyecto_Grupo_1.service.TortugaService;
import com.Proyecto_Grupo_1.service.UsuarioService;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.context.MessageSource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

/**
 * MVC contracts for the administrative views added for the operational modules.
 * These tests keep the services mocked and verify the exact model keys consumed by
 * each list/form template.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AdminModuleMvcTest {

    @Mock
    private AvistamientoService avistamientoService;
    @Mock
    private HerramientaService herramientaService;
    @Mock
    private PrestamoService prestamoService;
    @Mock
    private TortugaService tortugaService;
    @Mock
    private MonitoreoService monitoreoService;
    @Mock
    private NidoService nidoService;
    @Mock
    private NacimientoService nacimientoService;
    @Mock
    private EstadoService estadoService;
    @Mock
    private GuiaService guiaService;
    @Mock
    private UsuarioService usuarioService;
    @Mock
    private MessageSource messageSource;

    @BeforeEach
    void stubCatalogosCompartidos() {
        when(estadoService.getEstados(false)).thenReturn(List.of(new Estado()));
        when(tortugaService.getTortugas(false)).thenReturn(List.of(new Tortuga()));
        when(herramientaService.getHerramientas(false)).thenReturn(List.of(new Herramienta()));
        when(usuarioService.getUsuarios(false)).thenReturn(List.of(new Usuario()));
        when(guiaService.getGuias(false)).thenReturn(List.of(new Guia()));
        when(nidoService.getNidos(false)).thenReturn(List.of(new Nido()));
    }

    @Test
    void avistamientosExponenListadoYCatalogosDeFormulario() throws Exception {
        when(avistamientoService.getAvistamientos(false)).thenReturn(List.of(new Avistamiento()));
        MockMvc mvc = mvc(new AvistamientoController(
                avistamientoService, tortugaService, estadoService, messageSource));

        mvc.perform(get("/admin/avistamientos/listado"))
                .andExpect(status().isOk())
                .andExpect(view().name("/admin/avistamientos/listado"))
                .andExpect(model().attributeExists("avistamientos"))
                .andExpect(model().attribute("totalAvistamientos", 1));
        mvc.perform(get("/admin/avistamientos/nuevo"))
                .andExpect(status().isOk())
                .andExpect(view().name("/admin/avistamientos/modifica"))
                .andExpect(model().attributeExists("avistamiento", "tortugas", "estados"));
    }

    @Test
    void herramientasExponenListadoYCatalogoDeFormulario() throws Exception {
        when(herramientaService.getHerramientas(false)).thenReturn(List.of(new Herramienta()));
        MockMvc mvc = mvc(new HerramientaController(
                herramientaService, estadoService, messageSource));

        mvc.perform(get("/admin/herramientas/listado"))
                .andExpect(status().isOk())
                .andExpect(view().name("/admin/herramientas/listado"))
                .andExpect(model().attributeExists("herramientas"))
                .andExpect(model().attribute("totalHerramientas", 1));
        mvc.perform(get("/admin/herramientas/nuevo"))
                .andExpect(status().isOk())
                .andExpect(view().name("/admin/herramientas/modifica"))
                .andExpect(model().attributeExists("herramienta", "estados"));
    }

    @Test
    void prestamosExponenListadoYTodosLosCatalogosDeFormulario() throws Exception {
        when(prestamoService.getPrestamos(false)).thenReturn(List.of(new Prestamo()));
        MockMvc mvc = mvc(new PrestamoController(
                prestamoService, herramientaService, usuarioService, estadoService, messageSource));

        mvc.perform(get("/admin/prestamos/listado"))
                .andExpect(status().isOk())
                .andExpect(view().name("/admin/prestamos/listado"))
                .andExpect(model().attributeExists("prestamos"))
                .andExpect(model().attribute("totalPrestamos", 1));
        mvc.perform(get("/admin/prestamos/nuevo"))
                .andExpect(status().isOk())
                .andExpect(view().name("/admin/prestamos/modifica"))
                .andExpect(model().attributeExists("prestamo", "herramientas", "usuarios", "estados"));
    }

    @Test
    void tortugasExponenListadoYCatalogoDeFormulario() throws Exception {
        when(tortugaService.getTortugas(false)).thenReturn(List.of(new Tortuga()));
        MockMvc mvc = mvc(new TortugaController(
                tortugaService, estadoService, messageSource));

        mvc.perform(get("/admin/tortugas/listado"))
                .andExpect(status().isOk())
                .andExpect(view().name("/admin/tortugas/listado"))
                .andExpect(model().attributeExists("tortugas"))
                .andExpect(model().attribute("totalTortugas", 1));
        mvc.perform(get("/admin/tortugas/nuevo"))
                .andExpect(status().isOk())
                .andExpect(view().name("/admin/tortugas/modifica"))
                .andExpect(model().attributeExists("tortuga", "estados"));
    }

    @Test
    void monitoreosExponenListadoYCatalogosDeFormulario() throws Exception {
        when(monitoreoService.getMonitoreos(false)).thenReturn(List.of(new Monitoreo()));
        MockMvc mvc = mvc(new MonitoreoController(
                monitoreoService, guiaService, estadoService, messageSource));

        mvc.perform(get("/admin/monitoreos/listado"))
                .andExpect(status().isOk())
                .andExpect(view().name("/admin/monitoreos/listado"))
                .andExpect(model().attributeExists("monitoreos"))
                .andExpect(model().attribute("totalMonitoreos", 1));
        mvc.perform(get("/admin/monitoreos/nuevo"))
                .andExpect(status().isOk())
                .andExpect(view().name("/admin/monitoreos/modifica"))
                .andExpect(model().attributeExists("monitoreo", "guias", "estados"));
    }

    @Test
    void nidosExponenListadoYCatalogosDeFormulario() throws Exception {
        when(nidoService.getNidos(false)).thenReturn(List.of(new Nido()));
        MockMvc mvc = mvc(new NidoController(
                nidoService, tortugaService, estadoService, messageSource));

        mvc.perform(get("/admin/nidos/listado"))
                .andExpect(status().isOk())
                .andExpect(view().name("/admin/nidos/listado"))
                .andExpect(model().attributeExists("nidos"))
                .andExpect(model().attribute("totalNidos", 1));
        mvc.perform(get("/admin/nidos/nuevo"))
                .andExpect(status().isOk())
                .andExpect(view().name("/admin/nidos/modifica"))
                .andExpect(model().attributeExists("nido", "tortugas", "estados"));
    }

    @Test
    void nacimientosExponenListadoYCatalogosDeFormulario() throws Exception {
        when(nacimientoService.getNacimientos(false)).thenReturn(List.of(new Nacimiento()));
        MockMvc mvc = mvc(new NacimientoController(
                nacimientoService, nidoService, estadoService, messageSource));

        mvc.perform(get("/admin/nacimientos/listado"))
                .andExpect(status().isOk())
                .andExpect(view().name("/admin/nacimientos/listado"))
                .andExpect(model().attributeExists("nacimientos"))
                .andExpect(model().attribute("totalNacimientos", 1));
        mvc.perform(get("/admin/nacimientos/nuevo"))
                .andExpect(status().isOk())
                .andExpect(view().name("/admin/nacimientos/modifica"))
                .andExpect(model().attributeExists("nacimiento", "nidos", "estados"));
    }

    private MockMvc mvc(Object controller) {
        return MockMvcBuilders.standaloneSetup(controller)
                .setViewResolvers((viewName, locale) -> (model, request, response) -> {
                    // The companion WebMvcTest renders the real Thymeleaf views.
                    // This contract test only needs the returned view name and model.
                })
                .build();
    }
}
