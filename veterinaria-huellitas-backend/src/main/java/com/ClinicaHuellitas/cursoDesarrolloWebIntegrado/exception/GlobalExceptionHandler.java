package com.ClinicaHuellitas.cursoDesarrolloWebIntegrado.exception;

import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.ClinicaHuellitas.cursoDesarrolloWebIntegrado.dto.MensajeResponse;

/**
 * Convierte los errores en respuestas JSON con el formato { "mensaje": "..." }
 * (el mismo formato que ya usa el frontend) y el código HTTP correcto.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    // Errores de negocio lanzados desde los services (404, 409, 400, 403...)
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<MensajeResponse> manejarApiException(ApiException ex) {
        return ResponseEntity.status(ex.getStatus()).body(new MensajeResponse(ex.getMessage()));
    }

    // Falló alguna validación de los DTOs (@NotBlank, @Email, @Size...)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<MensajeResponse> manejarValidacion(MethodArgumentNotValidException ex) {
        String mensaje = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getDefaultMessage())
                .filter(Objects::nonNull)
                .collect(Collectors.joining(". "));
        return ResponseEntity.badRequest().body(new MensajeResponse(mensaje));
    }

    // JSON mal formado o fecha con formato incorrecto
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<MensajeResponse> manejarJsonInvalido(HttpMessageNotReadableException ex) {
        return ResponseEntity.badRequest().body(new MensajeResponse(
                "El cuerpo de la solicitud no es válido. Revisa el JSON y que la fecha tenga el formato yyyy-MM-ddTHH:mm:ss"));
    }

    // Parámetro con valor inválido, por ejemplo ?estado=XYZ
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<MensajeResponse> manejarParametroInvalido(MethodArgumentTypeMismatchException ex) {
        return ResponseEntity.badRequest().body(new MensajeResponse(
                "Valor inválido para el parámetro '" + ex.getName() + "'"));
    }

    // Login con correo o contraseña incorrectos
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<MensajeResponse> manejarCredencialesInvalidas(BadCredentialsException ex) {
        return ResponseEntity.status(401).body(new MensajeResponse("Correo o contraseña incorrectos"));
    }
}
