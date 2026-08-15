package com.Proyecto_Grupo_1.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Único dato editable de una reservación administrativa existente.
 */
@Data
public class EstadoReservacionForm {

    @NotNull
    private Integer idEstado;
}
