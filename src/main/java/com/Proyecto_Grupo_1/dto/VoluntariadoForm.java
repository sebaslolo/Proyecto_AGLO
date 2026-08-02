/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.Proyecto_Grupo_1.dto;

/*
 *
 * @author milu
 */

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;



@Data
public class VoluntariadoForm {
 @NotNull(message = "Debe seleccionar un voluntariado.")
 private Integer idActividad;
 @Size(max = 500, message = "Las herramientas no pueden superar los 500 caracteres.")
 private String herramientasUtilizadas;
}