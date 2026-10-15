package com.ClinicaHuellitas.cursoDesarrolloWebIntegrado.model;

/**
 * Estados posibles de una reserva.
 *
 * La reserva nace CONFIRMADA: el cliente NO necesita que el administrador la apruebe.
 * El sistema la valida solo (horario de atención, fecha futura y sin cruce de horario).
 */
public enum EstadoReserva {
    CONFIRMADA,
    CANCELADA,
    ATENDIDA
}
