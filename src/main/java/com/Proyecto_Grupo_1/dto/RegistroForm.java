package com.Proyecto_Grupo_1.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegistroForm {

    @NotBlank
    private String nombreCompleto;

    @Email
    @NotBlank
    private String correo;

    @NotBlank
    @Size(min = 8)
    private String password;

    @NotBlank
    private String confirmar;
}
