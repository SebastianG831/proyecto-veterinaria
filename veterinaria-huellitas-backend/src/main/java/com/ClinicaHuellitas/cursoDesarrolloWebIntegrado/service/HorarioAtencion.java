package com.ClinicaHuellitas.cursoDesarrolloWebIntegrado.service;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;

/**
 * Regla de negocio: horario de atención de la veterinaria.
 * Atiende de LUNES A SÁBADO (domingo cerrado), de "apertura" a "cierre"
 * (por defecto de 09:00 a 21:00). El servicio completo debe terminar antes del cierre.
 *
 * Es una clase Java simple (sin anotaciones de Spring) para poder probarla fácilmente.
 */
public class HorarioAtencion {

    private final LocalTime apertura;
    private final LocalTime cierre;

    public HorarioAtencion(LocalTime apertura, LocalTime cierre) {
        this.apertura = apertura;
        this.cierre = cierre;
    }

    /**
     * Revisa si una cita que empieza en "inicio" y termina en "fin" cumple el horario.
     *
     * @return vacío si es válida; si no, el mensaje que explica por qué no lo es
     */
    public Optional<String> validar(LocalDateTime inicio, LocalDateTime fin) {
        if (inicio.getDayOfWeek() == DayOfWeek.SUNDAY) {
            return Optional.of("No atendemos los domingos. Atendemos de lunes a sábado");
        }

        boolean terminaOtroDia = !fin.toLocalDate().equals(inicio.toLocalDate());
        boolean empiezaAntesDeAbrir = inicio.toLocalTime().isBefore(apertura);
        boolean terminaDespuesDeCerrar = fin.toLocalTime().isAfter(cierre);

        if (terminaOtroDia || empiezaAntesDeAbrir || terminaDespuesDeCerrar) {
            return Optional.of("El horario de atención es de lunes a sábado de " + apertura + " a " + cierre
                    + ", y el servicio debe terminar antes del cierre");
        }
        return Optional.empty();
    }
}
