package com.ClinicaHuellitas.cursoDesarrolloWebIntegrado.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ClinicaHuellitas.cursoDesarrolloWebIntegrado.dto.MensajeResponse;
import com.ClinicaHuellitas.cursoDesarrolloWebIntegrado.dto.UsuarioResponse;
import com.ClinicaHuellitas.cursoDesarrolloWebIntegrado.dto.UsuarioUpdateRequest;
import com.ClinicaHuellitas.cursoDesarrolloWebIntegrado.exception.ApiException;
import com.ClinicaHuellitas.cursoDesarrolloWebIntegrado.model.Rol;
import com.ClinicaHuellitas.cursoDesarrolloWebIntegrado.model.Usuario;
import com.ClinicaHuellitas.cursoDesarrolloWebIntegrado.repository.ReservaRepository;
import com.ClinicaHuellitas.cursoDesarrolloWebIntegrado.repository.RolRepository;
import com.ClinicaHuellitas.cursoDesarrolloWebIntegrado.repository.UsuarioRepository;
import com.ClinicaHuellitas.cursoDesarrolloWebIntegrado.util.EmailUtil;

/**
 * Gestión de usuarios (solo para el ADMIN): listar, ver, editar y eliminar.
 */
@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final ReservaRepository reservaRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository,
                          RolRepository rolRepository,
                          ReservaRepository reservaRepository,
                          PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.reservaRepository = reservaRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponse> listar() {
        return usuarioRepository.findAll().stream()
                .map(UsuarioResponse::desde)
                .toList();
    }

    @Transactional(readOnly = true)
    public UsuarioResponse obtener(Long id) {
        return UsuarioResponse.desde(buscarPorId(id));
    }

    @Transactional
    public UsuarioResponse actualizar(Long id, UsuarioUpdateRequest request, String emailSolicitante) {
        Usuario usuario = buscarPorId(id);
        boolean esElMismoAdmin = usuario.getEmail().equals(emailSolicitante);

        String nuevoEmail = EmailUtil.normalizar(request.email());
        if (usuarioRepository.existsByEmailAndIdNot(nuevoEmail, id)) {
            throw new ApiException(HttpStatus.CONFLICT, "Ese correo ya pertenece a otro usuario");
        }

        // El rol es opcional: si no viene, se mantiene el actual
        if (request.rol() != null && !request.rol().isBlank()) {
            String nombreRol = request.rol().trim().toUpperCase();
            Rol nuevoRol = rolRepository.findByNombre(nombreRol)
                    .orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST,
                            "El rol '" + request.rol() + "' no existe. Usa ADMIN o CLIENTE"));

            // Evita que un administrador se quite a sí mismo el acceso de administrador
            if (esElMismoAdmin && !nuevoRol.getNombre().equals(usuario.getRol().getNombre())) {
                throw new ApiException(HttpStatus.BAD_REQUEST, "No puedes cambiar tu propio rol");
            }
            usuario.setRol(nuevoRol);
        }

        usuario.setNombreCompleto(request.nombreCompleto().trim());
        usuario.setEmail(nuevoEmail);

        // La contraseña es opcional: solo se cambia si viene una nueva
        if (request.password() != null && !request.password().isBlank()) {
            usuario.setPassword(passwordEncoder.encode(request.password()));
        }

        return UsuarioResponse.desde(usuarioRepository.save(usuario));
    }

    @Transactional
    public MensajeResponse eliminar(Long id, String emailSolicitante) {
        Usuario usuario = buscarPorId(id);

        if (usuario.getEmail().equals(emailSolicitante)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "No puedes eliminar tu propia cuenta");
        }
        if (reservaRepository.existsByUsuarioId(id)) {
            throw new ApiException(HttpStatus.CONFLICT,
                    "No se puede eliminar: el usuario tiene reservas registradas");
        }

        usuarioRepository.delete(usuario);
        return new MensajeResponse("Usuario eliminado correctamente");
    }

    private Usuario buscarPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "El usuario no existe"));
    }
}
