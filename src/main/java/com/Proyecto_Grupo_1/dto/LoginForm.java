package com.Proyecto_Grupo_1.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginForm {

    @Email
    @NotBlank
    private String correo;

    @NotBlank
    private String password;
}
