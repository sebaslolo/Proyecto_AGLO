package com.Proyecto_Grupo_1.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@Entity
@Table(name = "fide_nido_tb")
public class Nido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_nido")
    private Integer idNido;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "etiqueta_tortuga", nullable = false)
    private Tortuga tortuga;

    @Column(name = "ubicacion", length = 100)
    private String ubicacion;

    @Column(name = "fecha_anidacion")
    private LocalDateTime fechaAnidacion;

    @Column(name = "cantidad_huevos")
    private Integer cantidadHuevos;

    @Column(name = "profundidad_nido")
    private Integer profundidadNido;

    @Column(name = "observaciones", length = 255)
    private String observaciones;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "id_estado", nullable = false)
    private Estado estado;

}