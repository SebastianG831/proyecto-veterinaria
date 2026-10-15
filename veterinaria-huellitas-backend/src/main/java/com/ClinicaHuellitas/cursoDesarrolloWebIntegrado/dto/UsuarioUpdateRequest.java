package com.ClinicaHuellitas.cursoDesarrolloWebIntegrado.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Datos que el administrador puede modificar de un usuario.
 * "password" y "rol" son opcionales: si vienen vacíos, se mantienen los actuales.
 */
public record UsuarioUpdateRequest(

        @NotBlank(message = "El nombre completo es obligatorio")
        @Size(max = 100, message = "El nombre completo no puede superar los 100 caracteres")
        String nombreCompleto,

        @NotBlank(message = "El correo es obligatorio")
        @Email(message = "El correo no tiene un formato válido")
        String email,

        @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
        String password,

        String rol // "ADMIN" o "CLIENTE"
) {
}
