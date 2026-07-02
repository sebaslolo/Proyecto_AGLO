package com.Proyecto_Grupo_1.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class ActividadGuiaId implements Serializable {

    @NotNull
    @Column(name = "id_actividad")
    private Integer idActividad;

    @NotNull
    @Column(name = "id_usuario")
    private Integer idUsuario;
}
