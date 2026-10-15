package com.ClinicaHuellitas.cursoDesarrolloWebIntegrado.dto;

/**
 * Respuesta del login: el token JWT y los datos básicos del usuario autenticado.
 */
public record AuthResponse(
        String token,
        String email,
        String nombreCompleto,
        String rol
) {
}
