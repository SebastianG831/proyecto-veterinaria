package com.ClinicaHuellitas.cursoDesarrolloWebIntegrado.dto;

import com.ClinicaHuellitas.cursoDesarrolloWebIntegrado.model.Servicio;

import java.math.BigDecimal;

public record ServicioResponse(
        Long id,
        String nombre,
        String descripcion,
        BigDecimal precio,
        int duracionMinutos,
        boolean activo
) {

    public static ServicioResponse desde(Servicio servicio) {
        return new ServicioResponse(
                servicio.getId(),
                servicio.getNombre(),
                servicio.getDescripcion(),
                servicio.getPrecio(),
                servicio.getDuracionMinutos(),
                servicio.isActivo()
        );
    }
}
