package com.ClinicaHuellitas.cursoDesarrolloWebIntegrado.repository;

import com.ClinicaHuellitas.cursoDesarrolloWebIntegrado.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByEmail(String email);

    boolean existsByEmail(String email);

    // Sirve para validar que al editar un usuario el correo no lo use otra persona
    boolean existsByEmailAndIdNot(String email, Long id);
}
