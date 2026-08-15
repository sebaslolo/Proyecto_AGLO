package com.Proyecto_Grupo_1.service;

import com.Proyecto_Grupo_1.domain.Actividad;
import com.Proyecto_Grupo_1.domain.Guia;
import com.Proyecto_Grupo_1.domain.Reservacion;
import com.Proyecto_Grupo_1.domain.Estado;
import com.Proyecto_Grupo_1.dto.DashboardViewModel;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Builds the read-only aggregate displayed by the administrative dashboard.
 * All source data continues to come from the established application services.
 */
@Service
public class DashboardService {

    private static final int DASHBOARD_LIST_LIMIT = 5;
    private static final int TREND_MONTHS = 6;
    private static final String ESTADO_ACTIVO = "Activo";
    private static final String ESTADO_SIN_NOMBRE = "Sin estado";

    private final UsuarioService usuarioService;
    private final ActividadService actividadService;
    private final ReservacionService reservacionService;
    private final GuiaService guiaService;

    public DashboardService(UsuarioService usuarioService,
            ActividadService actividadService,
            ReservacionService reservacionService,
            GuiaService guiaService) {
        this.usuarioService = usuarioService;
        this.actividadService = actividadService;
        this.reservacionService = reservacionService;
        this.guiaService = guiaService;
    }

    @Transactional(readOnly = true)
    public DashboardViewModel obtenerResumen() {
        return obtenerResumen(LocaleContextHolder.getLocale());
    }

    @Transactional(readOnly = true)
    public DashboardViewModel obtenerResumen(Locale locale) {
        Locale localeEfectivo = locale == null ? Locale.getDefault() : locale;
        LocalDateTime ahora = LocalDateTime.now();
        List<Actividad> actividadesFuturas = actividadesFuturas(ahora);
        List<Actividad> actividadesProximas = actividadesFuturas.stream()
                .limit(DASHBOARD_LIST_LIMIT)
                .toList();
        List<Reservacion> reservaciones = listaSegura(reservacionService.getReservaciones(false));
        List<Guia> guias = listaSegura(guiaService.getGuias(false));

        return new DashboardViewModel(
                listaSegura(usuarioService.getUsuarios(false)).size(),
                actividadesFuturas.size(),
                reservaciones.size(),
                montoReservado(reservaciones),
                actividadesProximas,
                reservacionesRecientes(reservaciones),
                reservacionesPorMes(reservaciones, localeEfectivo),
                reservacionesPorEstado(reservaciones),
                guias.size(),
                contarGuiasDisponibles(guias));
    }

    private List<Actividad> actividadesFuturas(LocalDateTime ahora) {
        return listaSegura(actividadService.getActividades(true)).stream()
                .filter(actividad -> actividad != null
                        && actividad.getFechaHoraInicio() != null
                        && actividad.getFechaHoraInicio().isAfter(ahora))
                .sorted(Comparator.comparing(Actividad::getFechaHoraInicio))
                .toList();
    }

    private BigDecimal montoReservado(List<Reservacion> reservaciones) {
        return reservaciones.stream()
                .filter(reservacion -> reservacion != null)
                .map(Reservacion::getMontoTotal)
                .filter(monto -> monto != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private List<Reservacion> reservacionesRecientes(List<Reservacion> reservaciones) {
        return reservaciones.stream()
                .filter(reservacion -> reservacion != null)
                .sorted(Comparator.comparing(
                        Reservacion::getFechaReservacion,
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(DASHBOARD_LIST_LIMIT)
                .toList();
    }

    private DashboardViewModel.ChartSeries reservacionesPorMes(List<Reservacion> reservaciones,
            Locale locale) {
        YearMonth mesActual = YearMonth.from(LocalDate.now());
        YearMonth primerMes = mesActual.minusMonths(TREND_MONTHS - 1L);
        Map<YearMonth, Long> conteos = new TreeMap<>();
        for (int indice = 0; indice < TREND_MONTHS; indice++) {
            conteos.put(primerMes.plusMonths(indice), 0L);
        }

        reservaciones.stream()
                .filter(reservacion -> reservacion != null && reservacion.getFechaReservacion() != null)
                .map(reservacion -> YearMonth.from(reservacion.getFechaReservacion()))
                .filter(conteos::containsKey)
                .forEach(mes -> conteos.computeIfPresent(mes, (clave, cantidad) -> cantidad + 1));

        DateTimeFormatter etiquetaMes = DateTimeFormatter.ofPattern("MMM yyyy", locale);
        return new DashboardViewModel.ChartSeries(
                conteos.keySet().stream()
                        .map(mes -> mes.atDay(1).format(etiquetaMes))
                        .toList(),
                List.copyOf(conteos.values()));
    }

    private DashboardViewModel.ChartSeries reservacionesPorEstado(List<Reservacion> reservaciones) {
        Map<String, Long> conteos = reservaciones.stream()
                .filter(reservacion -> reservacion != null)
                .collect(Collectors.groupingBy(
                        this::nombreEstado,
                        () -> new TreeMap<>(String.CASE_INSENSITIVE_ORDER),
                        Collectors.counting()));

        return new DashboardViewModel.ChartSeries(
                List.copyOf(conteos.keySet()),
                List.copyOf(conteos.values()));
    }

    private int contarGuiasDisponibles(List<Guia> guias) {
        return (int) guias.stream()
                .filter(guia -> guia != null)
                .filter(guia -> Boolean.TRUE.equals(guia.getDisponibilidad()))
                .filter(guia -> guia.getEstado() != null
                        && guia.getEstado().getNombreEstado() != null
                        && ESTADO_ACTIVO.equalsIgnoreCase(guia.getEstado().getNombreEstado().trim()))
                .count();
    }

    private String nombreEstado(Reservacion reservacion) {
        Estado estado = reservacion.getEstado();
        if (estado == null || estado.getNombreEstado() == null || estado.getNombreEstado().isBlank()) {
            return ESTADO_SIN_NOMBRE;
        }
        return estado.getNombreEstado().trim();
    }

    private <T> List<T> listaSegura(List<T> elementos) {
        return elementos == null ? List.of() : elementos;
    }
}
