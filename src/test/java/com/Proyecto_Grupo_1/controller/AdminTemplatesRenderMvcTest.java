package com.Proyecto_Grupo_1.controller;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Renders every administrative template introduced for the operational modules.
 * The controllers are exercised through MVC while all persistence-facing services
 * are mocked, so broken fragments, expressions, bindings or model contracts are
 * detected without requiring MySQL.
 */
@WebMvcTest(controllers = {
        AvistamientoController.class,
        HerramientaController.class,
        PrestamoController.class,
        TortugaController.class,
        MonitoreoController.class,
        NidoController.class,
        NacimientoController.class
})
@AutoConfigureMockMvc(addFilters = false)
class AdminTemplatesRenderMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AvistamientoService avistamientoService;
    @MockitoBean
    private HerramientaService herramientaService;
    @MockitoBean
    private PrestamoService prestamoService;
    @MockitoBean
    private TortugaService tortugaService;
    @MockitoBean
    private MonitoreoService monitoreoService;
    @MockitoBean
    private NidoService nidoService;
    @MockitoBean
    private NacimientoService nacimientoService;
    @MockitoBean
    private EstadoService estadoService;
    @MockitoBean
    private GuiaService guiaService;
    @MockitoBean
    private UsuarioService usuarioService;

    @BeforeEach
    void preparaCatalogosYListadosVacios() {
        when(avistamientoService.getAvistamientos(false)).thenReturn(List.of());
        when(herramientaService.getHerramientas(false)).thenReturn(List.of());
        when(prestamoService.getPrestamos(false)).thenReturn(List.of());
        when(tortugaService.getTortugas(false)).thenReturn(List.of());
        when(monitoreoService.getMonitoreos(false)).thenReturn(List.of());
        when(nidoService.getNidos(false)).thenReturn(List.of());
        when(nacimientoService.getNacimientos(false)).thenReturn(List.of());
        when(estadoService.getEstados(false)).thenReturn(List.of());
        when(guiaService.getGuias(false)).thenReturn(List.of());
        when(usuarioService.getUsuarios(false)).thenReturn(List.of());
    }

    @Test
    void renderizaLosCatorceListadosYFormulariosNuevos() throws Exception {
        for (String ruta : List.of(
                "/admin/avistamientos/listado", "/admin/avistamientos/nuevo",
                "/admin/herramientas/listado", "/admin/herramientas/nuevo",
                "/admin/prestamos/listado", "/admin/prestamos/nuevo",
                "/admin/tortugas/listado", "/admin/tortugas/nuevo",
                "/admin/monitoreos/listado", "/admin/monitoreos/nuevo",
                "/admin/nidos/listado", "/admin/nidos/nuevo",
                "/admin/nacimientos/listado", "/admin/nacimientos/nuevo")) {
            mockMvc.perform(get(ruta))
                    .andExpect(status().isOk())
                    .andExpect(content().string(containsString("admin.css")));
        }
    }
}
