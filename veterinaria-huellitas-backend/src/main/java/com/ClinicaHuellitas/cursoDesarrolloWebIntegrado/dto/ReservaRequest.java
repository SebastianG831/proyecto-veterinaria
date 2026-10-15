package com.ClinicaHuellitas.cursoDesarrolloWebIntegrado.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

/**
 * Datos que envía el cliente para reservar una cita.
 * "fechaHora" va en formato ISO, por ejemplo: 2026-10-20T10:00:00
 */
public record ReservaRequest(

        @NotNull(message = "Debes indicar el servicio")
        Long servicioId,

        @NotNull(message = "La fecha y hora son obligatorias")
        LocalDateTime fechaHora,

        @NotBlank(message = "El nombre de la mascota es obligatorio")
        @Size(max = 100, message = "El nombre de la mascota no puede superar los 100 caracteres")
        String nombreMascota,

        @Size(max = 500, message = "Las observaciones no pueden superar los 500 caracteres")
        String observaciones
) {
}
