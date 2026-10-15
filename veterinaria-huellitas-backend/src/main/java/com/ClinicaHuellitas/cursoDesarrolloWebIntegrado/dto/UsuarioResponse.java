package com.ClinicaHuellitas.cursoDesarrolloWebIntegrado.dto;

import com.ClinicaHuellitas.cursoDesarrolloWebIntegrado.model.Usuario;

/**
 * Datos de un usuario que se envían al cliente.
 * Nunca incluye la contraseña (por eso no se devuelve la entidad Usuario directamente).
 */
public record UsuarioResponse(
        Long id,
        String email,
        String nombreCompleto,
        String rol
) {

    public static UsuarioResponse desde(Usuario usuario) {
        return new UsuarioResponse(
                usuario.getId(),
                usuario.getEmail(),
                usuario.getNombreCompleto(),
                usuario.getRol().getNombre()
        );
    }
}
