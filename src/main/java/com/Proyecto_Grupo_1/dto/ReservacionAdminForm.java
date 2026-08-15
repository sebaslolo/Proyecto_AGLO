package com.Proyecto_Grupo_1.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Datos permitidos para crear una reservación desde la administración.
 * Relaciones, importes, fecha y estado se resuelven exclusivamente en el
 * servidor.
 */
@Data
public class ReservacionAdminForm {

    @NotNull
    private Integer idUsuario;

    @NotNull
    private Integer idActividad;

    @NotNull
    @Min(1)
    private Integer cantidadPersonas = 1;
}
