package com.ClinicaHuellitas.cursoDesarrolloWebIntegrado.repository;

import com.ClinicaHuellitas.cursoDesarrolloWebIntegrado.model.EstadoReserva;
import com.ClinicaHuellitas.cursoDesarrolloWebIntegrado.model.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    // Historial de un cliente: sus reservas, de la más reciente a la más antigua
    @Query("SELECT r FROM Reserva r JOIN FETCH r.servicio JOIN FETCH r.usuario "
            + "WHERE r.usuario.id = :usuarioId ORDER BY r.fechaHora DESC")
    List<Reserva> historialPorUsuario(@Param("usuarioId") Long usuarioId);

    // Panel del administrador: todas las reservas
    @Query("SELECT r FROM Reserva r JOIN FETCH r.servicio JOIN FETCH r.usuario "
            + "ORDER BY r.fechaHora DESC")
    List<Reserva> listarTodas();

    // Panel del administrador: reservas filtradas por estado
    @Query("SELECT r FROM Reserva r JOIN FETCH r.servicio JOIN FETCH r.usuario "
            + "WHERE r.estado = :estado ORDER BY r.fechaHora DESC")
    List<Reserva> listarPorEstado(@Param("estado") EstadoReserva estado);

    @Query("SELECT r FROM Reserva r JOIN FETCH r.servicio JOIN FETCH r.usuario WHERE r.id = :id")
    Optional<Reserva> buscarConDetalle(@Param("id") Long id);

    // Cuenta las reservas confirmadas del mismo servicio que se cruzan con [inicio, fin).
    // Dos horarios se cruzan cuando: inicioExistente < finNuevo  Y  finExistente > inicioNuevo
    @Query("SELECT COUNT(r) FROM Reserva r "
            + "WHERE r.servicio.id = :servicioId AND r.estado = :estado "
            + "AND r.fechaHora < :fin AND r.fechaHoraFin > :inicio")
    long contarCruces(@Param("servicioId") Long servicioId,
                      @Param("estado") EstadoReserva estado,
                      @Param("inicio") LocalDateTime inicio,
                      @Param("fin") LocalDateTime fin);

    boolean existsByUsuarioId(Long usuarioId);
}
