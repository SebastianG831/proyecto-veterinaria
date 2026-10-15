package com.ClinicaHuellitas.cursoDesarrolloWebIntegrado.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.ClinicaHuellitas.cursoDesarrolloWebIntegrado.repository.UsuarioRepository;
import com.ClinicaHuellitas.cursoDesarrolloWebIntegrado.util.EmailUtil;

/**
 * Spring Security llama a esta clase para buscar al usuario.
 * Aunque el método se llama "loadUserByUsername", aquí el "username" es el CORREO.
 */
@Service
public class UsuarioDetailsServiceImpl implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioDetailsServiceImpl(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        var usuario = usuarioRepository.findByEmail(EmailUtil.normalizar(email))
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + email));
        return new UsuarioDetailsImpl(usuario);
    }
}
