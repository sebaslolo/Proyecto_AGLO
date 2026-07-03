package com.Proyecto_Grupo_1.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@Entity
@Table(name = "fide_tipo_actividad_tb")
public class TipoActividad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tipo_actividad")
    private Integer idTipoActividad;

    @NotBlank
    @Column(name = "nombre_tipo_actividad", length = 100, nullable = false, unique = true)
    private String nombreTipoActividad;

    @NotBlank
    @Column(name = "descripcion_tipo_actividad", nullable = false)
    private String descripcionTipoActividad;

    @NotNull
    @Column(name = "precio_base", nullable = false)
    private BigDecimal precioBase;

    @NotBlank
    @Column(name = "duracion_estimada", length = 50, nullable = false)
    private String duracionEstimada;

    @Column(name = "imagen_tipo_actividad", length = 500)
    private String imagenTipoActividad;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "id_estado", nullable = false)
    private Estado estado;

    @Column(name = "fecha_creacion", insertable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_modificacion", insertable = false, updatable = false)
    private LocalDateTime fechaModificacion;
}
