package com.Proyecto_Grupo_1.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@Entity
@Table(name = "fide_tortuga_tb")
public class Tortuga {

    @Id
    @Column(name = "etiqueta_tortuga", length = 50)
    @NotBlank
    private String etiquetaTortuga;

    @NotBlank
    @Column(name = "especie", length = 100, nullable = false)
    private String especie;

    @NotBlank
    @Column(name = "sexo", length = 10, nullable = false)
    private String sexo;

    @Column(name = "fecha_registro")
    private LocalDateTime fechaRegistro;

    @Column(name = "observaciones", length = 255)
    private String observaciones;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "id_estado", nullable = false)
    private Estado estado;

}