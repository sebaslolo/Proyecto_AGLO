package com.Proyecto_Grupo_1.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RetroalimentacionForm {

    @NotNull(message = "Debe seleccionar una puntuación.")
    @Min(value = 1, message = "La puntuación mínima es 1.")
    @Max(value = 5, message = "La puntuación máxima es 5.")
    private Integer puntuacion;

    @Size(max = 1000, message = "El comentario no puede superar los 1000 caracteres.")
    private String comentarios;
}