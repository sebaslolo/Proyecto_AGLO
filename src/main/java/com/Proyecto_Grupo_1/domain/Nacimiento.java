package com.Proyecto_Grupo_1.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@Entity
@Table(name = "fide_nacimiento_tb")
public class Nacimiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_nacimiento")
    private Integer idNacimiento;


    @NotNull
    @ManyToOne
    @JoinColumn(name = "id_nido", nullable = false)
    private Nido nido;


    @Column(name = "fecha_eclosion")
    private LocalDateTime fechaEclosion;


    @Column(name = "crias_vivas")
    private Integer criasVivas;


    @Column(name = "crias_muertas")
    private Integer criasMuertas;


    @Column(name = "crias_infertiles")
    private Integer criasInfertiles;


    @Column(name = "observaciones", length = 255)
    private String observaciones;


    @NotNull
    @ManyToOne
    @JoinColumn(name = "id_estado", nullable = false)
    private Estado estado;

}