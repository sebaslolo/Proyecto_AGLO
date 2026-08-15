package com.Proyecto_Grupo_1.dto;

import com.Proyecto_Grupo_1.domain.Actividad;
import com.Proyecto_Grupo_1.domain.Reservacion;
import java.math.BigDecimal;
import java.util.List;

/**
 * Read-only data required to render the administrative dashboard.
 *
 * <p>The dashboard deliberately reuses the domain records exposed by the
 * existing services for its short lists. Chart data is kept separate so the
 * view can serialize it directly without recalculating business information.</p>
 */
public final class DashboardViewModel {

    private final int totalUsuarios;
    private final int totalActividadesProximas;
    private final int totalReservaciones;
    private final BigDecimal montoReservado;
    private final List<Actividad> actividadesProximas;
    private final List<Reservacion> reservacionesRecientes;
    private final ChartSeries reservacionesPorMes;
    private final ChartSeries reservacionesPorEstado;
    private final int totalGuias;
    private final int guiasDisponibles;

    public DashboardViewModel(int totalUsuarios,
            int totalActividadesProximas,
            int totalReservaciones,
            BigDecimal montoReservado,
            List<Actividad> actividadesProximas,
            List<Reservacion> reservacionesRecientes,
            ChartSeries reservacionesPorMes,
            ChartSeries reservacionesPorEstado,
            int totalGuias,
            int guiasDisponibles) {
        this.totalUsuarios = totalUsuarios;
        this.totalActividadesProximas = totalActividadesProximas;
        this.totalReservaciones = totalReservaciones;
        this.montoReservado = montoReservado;
        this.actividadesProximas = List.copyOf(actividadesProximas);
        this.reservacionesRecientes = List.copyOf(reservacionesRecientes);
        this.reservacionesPorMes = reservacionesPorMes;
        this.reservacionesPorEstado = reservacionesPorEstado;
        this.totalGuias = totalGuias;
        this.guiasDisponibles = guiasDisponibles;
    }

    public int getTotalUsuarios() {
        return totalUsuarios;
    }

    public int getTotalActividadesProximas() {
        return totalActividadesProximas;
    }

    public int getTotalReservaciones() {
        return totalReservaciones;
    }

    public BigDecimal getMontoReservado() {
        return montoReservado;
    }

    public List<Actividad> getActividadesProximas() {
        return actividadesProximas;
    }

    public List<Reservacion> getReservacionesRecientes() {
        return reservacionesRecientes;
    }

    public ChartSeries getReservacionesPorMes() {
        return reservacionesPorMes;
    }

    public ChartSeries getReservacionesPorEstado() {
        return reservacionesPorEstado;
    }

    public boolean isHasReservaciones() {
        return totalReservaciones > 0;
    }

    public int getTotalGuias() {
        return totalGuias;
    }

    public int getGuiasDisponibles() {
        return guiasDisponibles;
    }

    /** Backwards-compatible direct access for templates that use flat series. */
    public List<String> getEtiquetasTendencia() {
        return reservacionesPorMes.getLabels();
    }

    /** Backwards-compatible direct access for templates that use flat series. */
    public List<Long> getValoresTendencia() {
        return reservacionesPorMes.getValues();
    }

    /** Backwards-compatible direct access for templates that use flat series. */
    public List<String> getEtiquetasEstados() {
        return reservacionesPorEstado.getLabels();
    }

    /** Backwards-compatible direct access for templates that use flat series. */
    public List<Long> getValoresEstados() {
        return reservacionesPorEstado.getValues();
    }

    public static final class ChartSeries {

        private final List<String> labels;
        private final List<Long> values;

        public ChartSeries(List<String> labels, List<Long> values) {
            this.labels = List.copyOf(labels);
            this.values = List.copyOf(values);
        }

        public List<String> getLabels() {
            return labels;
        }

        public List<Long> getValues() {
            return values;
        }
    }
}
