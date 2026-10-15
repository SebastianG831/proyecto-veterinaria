package com.ClinicaHuellitas.cursoDesarrolloWebIntegrado.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Datos para iniciar sesión: el usuario ingresa con su correo y contraseña.
 */
public record LoginRequest(

        @NotBlank(message = "El correo es obligatorio")
        @Email(message = "El correo no tiene un formato válido")
        String email,

        @NotBlank(message = "La contraseña es obligatoria")
        String password
) {
}
