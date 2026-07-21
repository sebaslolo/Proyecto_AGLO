package com.Proyecto_Grupo_1.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReservacionForm {

    @NotNull
    private Integer idActividad;

    @NotNull
    @Min(1)
    private Integer cantidadPersonas = 1;
}
