package com.Proyecto_Grupo_1.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@Entity
@Table(name = "fide_retroalimentacion_tb")
public class Retroalimentacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_retroalimentacion")
    private Integer idRetroalimentacion;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "id_voluntariado", nullable = false)
    private Voluntariado voluntariado;

    @NotNull
    @Min(1)
    @Max(5)
    @Column(name = "puntuacion", nullable = false)
    private Integer puntuacion;

    @Column(name = "comentarios", columnDefinition = "text")
    private String comentarios;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "id_estado", nullable = false)
    private Estado estado;

    @Column(name = "fecha_creacion", insertable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_modificacion", insertable = false, updatable = false)
    private LocalDateTime fechaModificacion;
}