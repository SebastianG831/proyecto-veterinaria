package com.ClinicaHuellitas.cursoDesarrolloWebIntegrado.service;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ClinicaHuellitas.cursoDesarrolloWebIntegrado.dto.AuthResponse;
import com.ClinicaHuellitas.cursoDesarrolloWebIntegrado.dto.LoginRequest;
import com.ClinicaHuellitas.cursoDesarrolloWebIntegrado.dto.MensajeResponse;
import com.ClinicaHuellitas.cursoDesarrolloWebIntegrado.dto.RegistroRequest;
import com.ClinicaHuellitas.cursoDesarrolloWebIntegrado.exception.ApiException;
import com.ClinicaHuellitas.cursoDesarrolloWebIntegrado.model.Rol;
import com.ClinicaHuellitas.cursoDesarrolloWebIntegrado.model.Usuario;
import com.ClinicaHuellitas.cursoDesarrolloWebIntegrado.repository.RolRepository;
import com.ClinicaHuellitas.cursoDesarrolloWebIntegrado.repository.UsuarioRepository;
import com.ClinicaHuellitas.cursoDesarrolloWebIntegrado.security.JwtUtils;
import com.ClinicaHuellitas.cursoDesarrolloWebIntegrado.security.UsuarioDetailsImpl;
import com.ClinicaHuellitas.cursoDesarrolloWebIntegrado.util.EmailUtil;

/**
 * Lógica de registro e inicio de sesión.
 * Todo usuario nuevo se registra por defecto como CLIENTE (dueño de mascota).
 * Los ADMIN (staff) vienen en data.sql o los promueve otro ADMIN desde /api/admin/usuarios.
 */
@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    public AuthService(UsuarioRepository usuarioRepository,
                       RolRepository rolRepository,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager,
                       JwtUtils jwtUtils) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
    }

    @Transactional
    public MensajeResponse registrar(RegistroRequest request) {
        String email = EmailUtil.normalizar(request.email());

        if (usuarioRepository.existsByEmail(email)) {
            throw new ApiException(HttpStatus.CONFLICT, "El correo ya está registrado");
        }

        Rol rolCliente = rolRepository.findByNombre("CLIENTE")
                .orElseThrow(() -> new ApiException(HttpStatus.INTERNAL_SERVER_ERROR,
                        "El rol CLIENTE no existe. Revisa que data.sql se haya ejecutado"));

        Usuario usuario = new Usuario();
        usuario.setEmail(email);
        usuario.setPassword(passwordEncoder.encode(request.password()));
        usuario.setNombreCompleto(request.nombreCompleto().trim());
        usuario.setRol(rolCliente);
        usuarioRepository.save(usuario);

        return new MensajeResponse("Usuario registrado correctamente");
    }

    public AuthResponse login(LoginRequest request) {
        // Si el correo o la contraseña son incorrectos, lanza BadCredentialsException (401)
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password()));

        String token = jwtUtils.generateToken(authentication);
        Usuario usuario = ((UsuarioDetailsImpl) authentication.getPrincipal()).getUsuario();

        return new AuthResponse(
                token,
                usuario.getEmail(),
                usuario.getNombreCompleto(),
                usuario.getRol().getNombre()
        );
    }
}
