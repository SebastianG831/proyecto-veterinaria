package com.ClinicaHuellitas.cursoDesarrolloWebIntegrado.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Servicio que ofrece la veterinaria (consulta, vacunación, baño y corte, etc.).
 * No se borra de la base de datos: se "desactiva" (activo = false) para no perder
 * el historial de reservas que ya lo usaron.
 */
@Entity
@Table(name = "servicios")
public class Servicio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String nombre;

    @Column(length = 500)
    private String descripcion;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;

    // Cuánto dura el servicio; sirve para calcular la hora de fin de la reserva
    @Column(nullable = false)
    private int duracionMinutos;

    @Column(nullable = false)
    private boolean activo = true;

    // Un servicio puede estar en muchas reservas
    @OneToMany(mappedBy = "servicio")
    private List<Reserva> reservas = new ArrayList<>();

    public Servicio() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public int getDuracionMinutos() {
        return duracionMinutos;
    }

    public void setDuracionMinutos(int duracionMinutos) {
        this.duracionMinutos = duracionMinutos;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public List<Reserva> getReservas() {
        return reservas;
    }

    public void setReservas(List<Reserva> reservas) {
        this.reservas = reservas;
    }
}
