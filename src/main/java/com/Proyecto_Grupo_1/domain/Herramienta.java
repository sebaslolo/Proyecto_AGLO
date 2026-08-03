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
import java.time.LocalDateTime;
import lombok.Data;

@Data
@Entity
@Table(name = "fide_herramientas_tb")
public class Herramienta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_herramienta")
    private Integer idHerramienta;

    @NotBlank
    @Column(name = "nombre_herramienta", length = 100)
    private String nombreHerramienta;

    @Column(name = "descripcion", length = 255)
    private String descripcion;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "id_estado", nullable = false)
    private Estado estado;

    @Column(name = "fecha_creacion", insertable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_modificacion", insertable = false, updatable = false)
    private LocalDateTime fechaModificacion;
}
