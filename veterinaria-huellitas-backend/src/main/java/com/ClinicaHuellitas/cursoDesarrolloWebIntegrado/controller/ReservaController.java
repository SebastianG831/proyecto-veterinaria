package com.ClinicaHuellitas.cursoDesarrolloWebIntegrado.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ClinicaHuellitas.cursoDesarrolloWebIntegrado.dto.ReservaRequest;
import com.ClinicaHuellitas.cursoDesarrolloWebIntegrado.dto.ReservaResponse;
import com.ClinicaHuellitas.cursoDesarrolloWebIntegrado.service.ReservaService;

import jakarta.validation.Valid;

/**
 * Reservas del cliente: crear (se confirma sola), ver su historial y cancelar (con 24 h de anticipación).
 * "Authentication" trae al usuario que inició sesión; su nombre (getName) es el correo.
 */
@RestController
@RequestMapping("/api/reservas")
public class ReservaController {

    private final ReservaService reservaService;

    public ReservaController(ReservaService reservaService) {
        this.reservaService = reservaService;
    }

    @PostMapping
    public ResponseEntity<ReservaResponse> crear(@Valid @RequestBody ReservaRequest request,
                                                 Authentication authentication) {
        ReservaResponse reserva = reservaService.crear(authentication.getName(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(reserva);
    }

    @GetMapping("/mis-reservas")
    public List<ReservaResponse> misReservas(Authentication authentication) {
        return reservaService.historial(authentication.getName());
    }

    @PutMapping("/{id}/cancelar")
    public ReservaResponse cancelar(@PathVariable Long id, Authentication authentication) {
        return reservaService.cancelar(id, authentication.getName());
    }
}
