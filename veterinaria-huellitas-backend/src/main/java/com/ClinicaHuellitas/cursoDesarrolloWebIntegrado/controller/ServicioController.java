package com.ClinicaHuellitas.cursoDesarrolloWebIntegrado.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ClinicaHuellitas.cursoDesarrolloWebIntegrado.dto.MensajeResponse;
import com.ClinicaHuellitas.cursoDesarrolloWebIntegrado.dto.ServicioRequest;
import com.ClinicaHuellitas.cursoDesarrolloWebIntegrado.dto.ServicioResponse;
import com.ClinicaHuellitas.cursoDesarrolloWebIntegrado.service.ServicioService;

import jakarta.validation.Valid;

/**
 * CRUD de servicios.
 * Consultar (GET): ADMIN y CLIENTE.  Crear, editar y eliminar: solo ADMIN (ver SecurityConfig).
 */
@RestController
@RequestMapping("/api/servicios")
public class ServicioController {

    private final ServicioService servicioService;

    public ServicioController(ServicioService servicioService) {
        this.servicioService = servicioService;
    }

    @GetMapping
    public List<ServicioResponse> listar() {
        return servicioService.listarActivos();
    }

    @GetMapping("/buscar")
    public List<ServicioResponse> buscar(@RequestParam String nombre) {
        return servicioService.buscar(nombre);
    }

    @GetMapping("/{id}")
    public ServicioResponse obtener(@PathVariable Long id) {
        return servicioService.obtener(id);
    }

    @PostMapping
    public ResponseEntity<ServicioResponse> crear(@Valid @RequestBody ServicioRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(servicioService.crear(request));
    }

    @PutMapping("/{id}")
    public ServicioResponse actualizar(@PathVariable Long id, @Valid @RequestBody ServicioRequest request) {
        return servicioService.actualizar(id, request);
    }

    @DeleteMapping("/{id}")
    public MensajeResponse eliminar(@PathVariable Long id) {
        return servicioService.desactivar(id);
    }
}
