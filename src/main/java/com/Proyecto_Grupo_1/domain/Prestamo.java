package com.Proyecto_Grupo_1.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.Data;

@Data
@Entity
@Table(name = "fide_prestamo_tb")
public class Prestamo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_prestamo")
    private Integer idPrestamo;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "id_herramienta")
    private Herramienta herramienta;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    @Column(name = "fecha_prestamo")
    private LocalDate fechaPrestamo;

    @Column(name = "fecha_devolucion")
    private LocalDate fechaDevolucion;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "id_estado")
    private Estado estado;

}