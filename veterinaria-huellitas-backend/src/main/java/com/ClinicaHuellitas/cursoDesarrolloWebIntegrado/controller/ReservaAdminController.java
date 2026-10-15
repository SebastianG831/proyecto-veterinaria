package com.ClinicaHuellitas.cursoDesarrolloWebIntegrado.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ClinicaHuellitas.cursoDesarrolloWebIntegrado.dto.ReservaResponse;
import com.ClinicaHuellitas.cursoDesarrolloWebIntegrado.model.EstadoReserva;
import com.ClinicaHuellitas.cursoDesarrolloWebIntegrado.service.ReservaService;

/**
 * Supervisión de reservas para el administrador (ruta /api/admin/**: solo ADMIN).
 * El administrador NO aprueba las reservas: ellas se confirman solas.
 */
@RestController
@RequestMapping("/api/admin/reservas")
public class ReservaAdminController {

    private final ReservaService reservaService;

    public ReservaAdminController(ReservaService reservaService) {
        this.reservaService = reservaService;
    }

    // GET /api/admin/reservas            -> todas
    // GET /api/admin/reservas?estado=... -> filtradas (CONFIRMADA, CANCELADA o ATENDIDA)
    @GetMapping
    public List<ReservaResponse> listar(@RequestParam(required = false) EstadoReserva estado) {
        return reservaService.listarTodas(estado);
    }

    @PutMapping("/{id}/atendida")
    public ReservaResponse marcarAtendida(@PathVariable Long id) {
        return reservaService.marcarAtendida(id);
    }
}
