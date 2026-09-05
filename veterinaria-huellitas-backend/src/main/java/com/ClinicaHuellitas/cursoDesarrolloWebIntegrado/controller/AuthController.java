package com.ClinicaHuellitas.cursoDesarrolloWebIntegrado.controller;

import com.ClinicaHuellitas.cursoDesarrolloWebIntegrado.dto.*;
import com.ClinicaHuellitas.cursoDesarrolloWebIntegrado.model.*;
import com.ClinicaHuellitas.cursoDesarrolloWebIntegrado.repository.*;
import com.ClinicaHuellitas.cursoDesarrolloWebIntegrado.security.JwtUtils;
import com.ClinicaHuellitas.cursoDesarrolloWebIntegrado.security.UsuarioDetailsImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

/**
 * Autenticación y registro para Veterinaria Huellitas.
 * Todo usuario nuevo se registra por defecto como CLIENTE (dueño de mascota).
 * Los usuarios con rol ADMIN (staff de la veterinaria) deben crearse manualmente
 * en la base de datos o mediante un endpoint administrativo futuro.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtils jwtUtils;

    @PostMapping("/registro")
    public ResponseEntity<?> registrar(@Valid @RequestBody RegistroRequest request) {

        if (usuarioRepository.existsByUsername(request.getUsername())) {
            return ResponseEntity.badRequest().body(new MensajeResponse("El username ya está en uso"));
        }
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity.badRequest().body(new MensajeResponse("El email ya está registrado"));
        }

        Rol rolCliente = rolRepository.findByNombre("CLIENTE")
                .orElseThrow(() -> new RuntimeException("Rol CLIENTE no existe. Debes insertarlo primero en la BD."));

        Usuario usuario = new Usuario();
        usuario.setUsername(request.getUsername());
        usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        usuario.setEmail(request.getEmail());
        usuario.setNombreCompleto(request.getNombreCompleto());
        usuario.setRol(rolCliente);
        usuarioRepository.save(usuario);

        return ResponseEntity.ok(new MensajeResponse("Usuario registrado correctamente"));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        String token = jwtUtils.generateToken(authentication);
        UsuarioDetailsImpl userDetails = (UsuarioDetailsImpl) authentication.getPrincipal();

        return ResponseEntity.ok(new AuthResponse(
                token,
                userDetails.getUsername(),
                userDetails.getUsuario().getRol().getNombre()
        ));
    }
}
