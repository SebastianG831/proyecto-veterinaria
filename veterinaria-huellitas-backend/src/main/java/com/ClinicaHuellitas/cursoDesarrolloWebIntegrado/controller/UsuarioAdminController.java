package com.ClinicaHuellitas.cursoDesarrolloWebIntegrado.controller;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ClinicaHuellitas.cursoDesarrolloWebIntegrado.dto.MensajeResponse;
import com.ClinicaHuellitas.cursoDesarrolloWebIntegrado.dto.UsuarioResponse;
import com.ClinicaHuellitas.cursoDesarrolloWebIntegrado.dto.UsuarioUpdateRequest;
import com.ClinicaHuellitas.cursoDesarrolloWebIntegrado.service.UsuarioService;

import jakarta.validation.Valid;

/**
 * Gestión de usuarios (ruta /api/admin/**: solo ADMIN).
 * Los usuarios se crean desde el registro público (/api/auth/registro).
 */
@RestController
@RequestMapping("/api/admin/usuarios")
public class UsuarioAdminController {

    private final UsuarioService usuarioService;

    public UsuarioAdminController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public List<UsuarioResponse> listar() {
        return usuarioService.listar();
    }

    @GetMapping("/{id}")
    public UsuarioResponse obtener(@PathVariable Long id) {
        return usuarioService.obtener(id);
    }

    @PutMapping("/{id}")
    public UsuarioResponse actualizar(@PathVariable Long id,
                                      @Valid @RequestBody UsuarioUpdateRequest request,
                                      Authentication authentication) {
        return usuarioService.actualizar(id, request, authentication.getName());
    }

    @DeleteMapping("/{id}")
    public MensajeResponse eliminar(@PathVariable Long id, Authentication authentication) {
        return usuarioService.eliminar(id, authentication.getName());
    }
}
