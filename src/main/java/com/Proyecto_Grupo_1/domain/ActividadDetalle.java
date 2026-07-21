package com.Proyecto_Grupo_1.domain;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@Entity
@Table(name = "fide_actividad_detalle_tb")
public class ActividadDetalle {

    @Valid
    @NotNull
    @EmbeddedId
    private ActividadDetalleId id = new ActividadDetalleId();

    @NotNull
    @ManyToOne
    @MapsId("idReservacion")
    @JoinColumn(name = "id_reservacion", nullable = false)
    private Reservacion reservacion;

    @NotNull
    @ManyToOne
    @MapsId("idActividad")
    @JoinColumn(name = "id_actividad", nullable = false)
    private Actividad actividad;

    @NotNull
    @Column(name = "cantidad_personas", nullable = false)
    private Integer cantidadPersonas;

    @NotNull
    @Column(name = "precio_unitario", nullable = false)
    private BigDecimal precioUnitario;

    @NotNull
    @Column(name = "subtotal", nullable = false)
    private BigDecimal subtotal;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "id_estado", nullable = false)
    private Estado estado;

    @Column(name = "fecha_creacion", insertable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_modificacion", insertable = false, updatable = false)
    private LocalDateTime fechaModificacion;
}
