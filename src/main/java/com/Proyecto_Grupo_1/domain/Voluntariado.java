/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.Proyecto_Grupo_1.domain;

/*
 *
 * @author milu
 */


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@Entity
@Table(name = "fide_voluntariado_tb")
public class Voluntariado {
    @Id
 @GeneratedValue(strategy = GenerationType.IDENTITY)
 @Column(name = "id_voluntariado")
 private Integer idVoluntariado;
 @ManyToOne
 @JoinColumn(name = "id_usuario", nullable = false)
 private Usuario usuario;
 @ManyToOne
 @JoinColumn(name = "id_actividad", nullable = false)
 private Actividad actividad;
 @Column(name = "herramientas_utilizadas", length = 500)
 private String herramientasUtilizadas;
 @Column(name = "fecha_inscripcion", nullable = false)
 private LocalDateTime fechaInscripcion;
 @Column(name = "fecha_creacion", insertable = false, updatable = false)
 private LocalDateTime fechaCreacion;
 @Column(name = "fecha_modificacion", insertable = false, updatable = false)
 private LocalDateTime fechaModificacion;
}
    


