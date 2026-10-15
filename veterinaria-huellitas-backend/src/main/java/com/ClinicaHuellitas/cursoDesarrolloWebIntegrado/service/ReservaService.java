package com.ClinicaHuellitas.cursoDesarrolloWebIntegrado.service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ClinicaHuellitas.cursoDesarrolloWebIntegrado.dto.ReservaRequest;
import com.ClinicaHuellitas.cursoDesarrolloWebIntegrado.dto.ReservaResponse;
import com.ClinicaHuellitas.cursoDesarrolloWebIntegrado.exception.ApiException;
import com.ClinicaHuellitas.cursoDesarrolloWebIntegrado.model.EstadoReserva;
import com.ClinicaHuellitas.cursoDesarrolloWebIntegrado.model.Reserva;
import com.ClinicaHuellitas.cursoDesarrolloWebIntegrado.model.Servicio;
import com.ClinicaHuellitas.cursoDesarrolloWebIntegrado.model.Usuario;
import com.ClinicaHuellitas.cursoDesarrolloWebIntegrado.repository.ReservaRepository;
import com.ClinicaHuellitas.cursoDesarrolloWebIntegrado.repository.ServicioRepository;
import com.ClinicaHuellitas.cursoDesarrolloWebIntegrado.repository.UsuarioRepository;

/**
 * Reglas de negocio de las reservas.
 *
 * REGLA CLAVE: el cliente NO necesita que el administrador valide su cita.
 * La reserva se CONFIRMA automáticamente si el sistema comprueba que:
 *   1. el servicio existe y está activo,
 *   2. la fecha y hora son futuras,
 *   3. está dentro del horario de atención (lunes a sábado, 9:00 a 21:00),
 *   4. no se cruza con otra reserva confirmada del mismo servicio.
 * El administrador solo supervisa: ve todas las reservas y las marca como atendidas.
 * NO puede cancelarlas: solo el cliente, con 24 horas de anticipación.
 */
@Service
public class ReservaService {

    // Anticipación mínima (en horas) para que el cliente pueda cancelar su reserva
    private static final long HORAS_MINIMAS_PARA_CANCELAR = 24;

    private final ReservaRepository reservaRepository;
    private final ServicioRepository servicioRepository;
    private final UsuarioRepository usuarioRepository;
    private final HorarioAtencion horarioAtencion;
    private final Clock clock;

    public ReservaService(ReservaRepository reservaRepository,
                          ServicioRepository servicioRepository,
                          UsuarioRepository usuarioRepository,
                          HorarioAtencion horarioAtencion,
                          Clock clock) {
        this.reservaRepository = reservaRepository;
        this.servicioRepository = servicioRepository;
        this.usuarioRepository = usuarioRepository;
        this.horarioAtencion = horarioAtencion;
        this.clock = clock;
    }

    @Transactional
    public ReservaResponse crear(String emailCliente, ReservaRequest request) {
        Usuario cliente = buscarUsuario(emailCliente);

        Servicio servicio = servicioRepository.findById(request.servicioId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "El servicio no existe"));
        if (!servicio.isActivo()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "El servicio no está disponible");
        }

        LocalDateTime inicio = request.fechaHora();
        LocalDateTime fin = inicio.plusMinutes(servicio.getDuracionMinutos());

        if (!inicio.isAfter(ahora())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "La fecha y hora de la reserva deben ser futuras");
        }

        horarioAtencion.validar(inicio, fin).ifPresent(motivo -> {
            throw new ApiException(HttpStatus.BAD_REQUEST, motivo);
        });

        long cruces = reservaRepository.contarCruces(servicio.getId(), EstadoReserva.CONFIRMADA, inicio, fin);
        if (cruces > 0) {
            throw new ApiException(HttpStatus.CONFLICT,
                    "Ese horario ya está ocupado para este servicio. Elige otra hora");
        }

        // Todo en orden: la reserva nace CONFIRMADA, sin aprobación del administrador
        Reserva reserva = new Reserva();
        reserva.setUsuario(cliente);
        reserva.setServicio(servicio);
        reserva.setFechaHora(inicio);
        reserva.setFechaHoraFin(fin);
        reserva.setNombreMascota(request.nombreMascota().trim());
        reserva.setObservaciones(request.observaciones());
        reserva.setEstado(EstadoReserva.CONFIRMADA);
        reserva.setCreadaEn(ahora());

        return ReservaResponse.desde(reservaRepository.save(reserva));
    }

    @Transactional(readOnly = true)
    public List<ReservaResponse> historial(String emailCliente) {
        Usuario cliente = buscarUsuario(emailCliente);
        return reservaRepository.historialPorUsuario(cliente.getId()).stream()
                .map(ReservaResponse::desde)
                .toList();
    }

    /**
     * Cancela una reserva. Reglas:
     *   - Solo el CLIENTE dueño de la reserva puede cancelarla (el administrador NO puede).
     *   - Debe hacerse con al menos 24 horas de anticipación a la hora programada.
     *   - Solo se pueden cancelar reservas CONFIRMADAS.
     */
    @Transactional
    public ReservaResponse cancelar(Long id, String emailSolicitante) {
        Reserva reserva = buscarReserva(id);

        if (!reserva.getUsuario().getEmail().equals(emailSolicitante)) {
            throw new ApiException(HttpStatus.FORBIDDEN, "No puedes cancelar la reserva de otro cliente");
        }
        if (reserva.getEstado() != EstadoReserva.CONFIRMADA) {
            throw new ApiException(HttpStatus.CONFLICT, "Solo se pueden cancelar reservas confirmadas");
        }
        if (ahora().isAfter(reserva.getFechaHora().minusHours(HORAS_MINIMAS_PARA_CANCELAR))) {
            throw new ApiException(HttpStatus.BAD_REQUEST,
                    "Solo puedes cancelar una reserva con al menos " + HORAS_MINIMAS_PARA_CANCELAR
                            + " horas de anticipación");
        }

        reserva.setEstado(EstadoReserva.CANCELADA);
        return ReservaResponse.desde(reservaRepository.save(reserva));
    }

    // ----- Panel del administrador -----

    @Transactional(readOnly = true)
    public List<ReservaResponse> listarTodas(EstadoReserva estado) {
        List<Reserva> reservas = (estado == null)
                ? reservaRepository.listarTodas()
                : reservaRepository.listarPorEstado(estado);
        return reservas.stream()
                .map(ReservaResponse::desde)
                .toList();
    }

    @Transactional
    public ReservaResponse marcarAtendida(Long id) {
        Reserva reserva = buscarReserva(id);

        if (reserva.getEstado() != EstadoReserva.CONFIRMADA) {
            throw new ApiException(HttpStatus.CONFLICT,
                    "Solo se pueden marcar como atendidas las reservas confirmadas");
        }

        reserva.setEstado(EstadoReserva.ATENDIDA);
        return ReservaResponse.desde(reservaRepository.save(reserva));
    }

    // ----- Métodos auxiliares -----

    private LocalDateTime ahora() {
        return LocalDateTime.now(clock);
    }

    private Usuario buscarUsuario(String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
    }

    private Reserva buscarReserva(Long id) {
        return reservaRepository.buscarConDetalle(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "La reserva no existe"));
    }
}
