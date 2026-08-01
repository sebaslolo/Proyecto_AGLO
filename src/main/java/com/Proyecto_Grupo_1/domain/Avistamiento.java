package com.Proyecto_Grupo_1.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@Entity
@Table(name = "fide_avistamiento_tb")
public class Avistamiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_avistamiento")
    private Integer idAvistamiento;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "etiqueta_tortuga", nullable = false)
    private Tortuga tortuga;

    @NotBlank
    @Column(name = "comportamiento", length = 100)
    private String comportamiento;

    @NotBlank
    @Column(name = "ubicacion", length = 100)
    private String ubicacion;

    @Column(name = "fecha_avistamiento")
    private LocalDateTime fechaAvistamiento;

    @Column(name = "observaciones", length = 255)
    private String observaciones;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "id_estado", nullable = false)
    private Estado estado;

}