package com.ClinicaHuellitas.cursoDesarrolloWebIntegrado.dto;

import com.ClinicaHuellitas.cursoDesarrolloWebIntegrado.model.EstadoReserva;
import com.ClinicaHuellitas.cursoDesarrolloWebIntegrado.model.Reserva;

import java.time.LocalDateTime;

public record ReservaResponse(
        Long id,
        Long servicioId,
        String servicioNombre,
        String clienteEmail,
        String clienteNombre,
        String nombreMascota,
        LocalDateTime fechaHora,
        LocalDateTime fechaHoraFin,
        EstadoReserva estado,
        String observaciones
) {

    public static ReservaResponse desde(Reserva reserva) {
        return new ReservaResponse(
                reserva.getId(),
                reserva.getServicio().getId(),
                reserva.getServicio().getNombre(),
                reserva.getUsuario().getEmail(),
                reserva.getUsuario().getNombreCompleto(),
                reserva.getNombreMascota(),
                reserva.getFechaHora(),
                reserva.getFechaHoraFin(),
                reserva.getEstado(),
                reserva.getObservaciones()
        );
    }
}
