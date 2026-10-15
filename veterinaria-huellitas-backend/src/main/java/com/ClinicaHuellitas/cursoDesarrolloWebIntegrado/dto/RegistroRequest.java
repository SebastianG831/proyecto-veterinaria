package com.ClinicaHuellitas.cursoDesarrolloWebIntegrado.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Datos para registrar un nuevo usuario (siempre se crea con rol CLIENTE).
 * Ya no se pide "username": el correo es el identificador de inicio de sesión.
 */
public record RegistroRequest(

        @NotBlank(message = "El nombre completo es obligatorio")
        @Size(max = 100, message = "El nombre completo no puede superar los 100 caracteres")
        String nombreCompleto,

        @NotBlank(message = "El correo es obligatorio")
        @Email(message = "El correo no tiene un formato válido")
        String email,

        @NotBlank(message = "La contraseña es obligatoria")
        @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
        String password
) {
}
