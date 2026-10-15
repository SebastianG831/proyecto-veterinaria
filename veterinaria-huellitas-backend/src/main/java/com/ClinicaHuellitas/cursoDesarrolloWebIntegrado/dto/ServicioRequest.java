package com.ClinicaHuellitas.cursoDesarrolloWebIntegrado.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/**
 * Datos para crear o editar un servicio.
 * "activo" es opcional: sirve para volver a activar un servicio desactivado.
 */
public record ServicioRequest(

        @NotBlank(message = "El nombre del servicio es obligatorio")
        @Size(max = 100, message = "El nombre no puede superar los 100 caracteres")
        String nombre,

        @Size(max = 500, message = "La descripción no puede superar los 500 caracteres")
        String descripcion,

        @NotNull(message = "El precio es obligatorio")
        @DecimalMin(value = "0.01", message = "El precio debe ser mayor a 0")
        BigDecimal precio,

        @NotNull(message = "La duración en minutos es obligatoria")
        @Min(value = 5, message = "La duración mínima es de 5 minutos")
        @Max(value = 480, message = "La duración máxima es de 480 minutos")
        Integer duracionMinutos,

        Boolean activo
) {
}
