package com.ClinicaHuellitas.cursoDesarrolloWebIntegrado.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ClinicaHuellitas.cursoDesarrolloWebIntegrado.dto.AuthResponse;
import com.ClinicaHuellitas.cursoDesarrolloWebIntegrado.dto.LoginRequest;
import com.ClinicaHuellitas.cursoDesarrolloWebIntegrado.dto.MensajeResponse;
import com.ClinicaHuellitas.cursoDesarrolloWebIntegrado.dto.RegistroRequest;
import com.ClinicaHuellitas.cursoDesarrolloWebIntegrado.service.AuthService;

import jakarta.validation.Valid;

/**
 * Registro e inicio de sesión (endpoints públicos).
 * El controller solo recibe la petición y delega la lógica en AuthService.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/registro")
    public ResponseEntity<MensajeResponse> registrar(@Valid @RequestBody RegistroRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.registrar(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}
