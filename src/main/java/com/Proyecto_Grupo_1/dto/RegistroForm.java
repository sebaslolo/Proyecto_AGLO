package com.Proyecto_Grupo_1.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegistroForm {

    @NotBlank
    @Size(max = 30)
    private String username;

    @NotBlank
    @Size(max = 20)
    private String nombre;

    @NotBlank
    @Size(max = 30)
    private String apellidoPaterno;

    @Size(max = 30)
    private String apellidoMaterno;

    @Email
    @NotBlank
    @Size(max = 75)
    private String correo;

    @Size(max = 25)
    private String telefono;

    @NotBlank
    @Size(min = 8, max = 512)
    private String password;

    @NotBlank
    private String confirmar;
}
