package com.ClinicaHuellitas.cursoDesarrolloWebIntegrado.exception;

import org.springframework.http.HttpStatus;

/**
 * Error de negocio con el código HTTP que debe recibir el cliente
 * (por ejemplo 404 si algo no existe, 409 si hay un conflicto).
 * Lo captura GlobalExceptionHandler y lo convierte en una respuesta JSON.
 */
public class ApiException extends RuntimeException {

    private final HttpStatus status;

    public ApiException(HttpStatus status, String mensaje) {
        super(mensaje);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
