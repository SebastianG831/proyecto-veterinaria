package com.ClinicaHuellitas.cursoDesarrolloWebIntegrado.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Usuario del sistema de Veterinaria Huellitas.
 * Puede ser un ADMIN (staff de la veterinaria) o un CLIENTE (dueño de mascota
 * que reserva citas para su(s) mascota(s)).
 *
 * El usuario inicia sesión con su CORREO (no existe un "username" aparte).
 */
@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // El correo identifica al usuario: es único y se usa para iniciar sesión
    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String nombreCompleto;

    @ManyToOne
    @JoinColumn(name = "rol_id", nullable = false)
    private Rol rol;

    // Un usuario (cliente) puede tener muchas reservas
    @OneToMany(mappedBy = "usuario")
    private List<Reserva> reservas = new ArrayList<>();

    public Usuario() {
    }

    public Usuario(Long id, String email, String password, String nombreCompleto, Rol rol) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.nombreCompleto = nombreCompleto;
        this.rol = rol;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }

    public List<Reserva> getReservas() {
        return reservas;
    }

    public void setReservas(List<Reserva> reservas) {
        this.reservas = reservas;
    }
}
