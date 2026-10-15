// Modelos que devuelve el backend (ver los DTO *Response de Spring Boot)

export type EstadoReserva = 'CONFIRMADA' | 'CANCELADA' | 'ATENDIDA';

export interface Servicio {
  id: number;
  nombre: string;
  descripcion: string | null;
  precio: number;
  duracionMinutos: number;
  activo: boolean;
}

export interface ServicioRequest {
  nombre: string;
  descripcion: string;
  precio: number;
  duracionMinutos: number;
  activo?: boolean;
}

export interface Reserva {
  id: number;
  servicioId: number;
  servicioNombre: string;
  clienteEmail: string;
  clienteNombre: string;
  nombreMascota: string;
  fechaHora: string; // ISO local: 2026-10-20T10:00:00
  fechaHoraFin: string;
  estado: EstadoReserva;
  observaciones: string | null;
}

export interface ReservaRequest {
  servicioId: number;
  fechaHora: string;
  nombreMascota: string;
  observaciones?: string;
}

export interface Usuario {
  id: number;
  email: string;
  nombreCompleto: string;
  rol: 'ADMIN' | 'CLIENTE';
}

export interface UsuarioUpdateRequest {
  nombreCompleto: string;
  email: string;
  password?: string;
  rol: string;
}
