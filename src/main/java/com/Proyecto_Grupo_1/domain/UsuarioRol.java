package com.Proyecto_Grupo_1.domain;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@Entity
@Table(name = "fide_usuario_rol_tb")
public class UsuarioRol {

    @Valid
    @NotNull
    @EmbeddedId
    private UsuarioRolId id = new UsuarioRolId();

    @NotNull
    @ManyToOne
    @MapsId("idUsuario")
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @NotNull
    @ManyToOne
    @MapsId("idRol")
    @JoinColumn(name = "id_rol", nullable = false)
    private Rol rol;

    @Column(name = "fecha_creacion", insertable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_modificacion", insertable = false, updatable = false)
    private LocalDateTime fechaModificacion;
}
