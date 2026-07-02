package com.Proyecto_Grupo_1.domain;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@Entity
@Table(name = "fide_actividad_guia_tb")
public class ActividadGuia {

    @Valid
    @NotNull
    @EmbeddedId
    private ActividadGuiaId id = new ActividadGuiaId();

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("idActividad")
    @JoinColumn(name = "id_actividad", nullable = false)
    private Actividad actividad;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("idGuia")
    @JoinColumn(name = "id_guia", nullable = false)
    private Guia guia;

    @Column(name = "fecha_asignacion")
    private LocalDateTime fechaAsignacion;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_estado", nullable = false)
    private Estado estado;

    @Column(name = "fecha_creacion", insertable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_modificacion", insertable = false, updatable = false)
    private LocalDateTime fechaModificacion;
}
