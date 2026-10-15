import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import {
  EstadoReserva, Reserva, ReservaRequest, Servicio, ServicioRequest, Usuario, UsuarioUpdateRequest,
} from '../models/api.models';
import { MensajeResponse } from './auth.service';

/** Todos los endpoints del backend usados por las pantallas (ver README del backend). */
@Injectable({ providedIn: 'root' })
export class ApiService {
  private http = inject(HttpClient);
  private readonly API = 'http://localhost:8085/api';

  // ----- Servicios -----
  listarServicios(): Observable<Servicio[]> {
    return this.http.get<Servicio[]>(`${this.API}/servicios`);
  }
  crearServicio(data: ServicioRequest): Observable<Servicio> {
    return this.http.post<Servicio>(`${this.API}/servicios`, data);
  }
  actualizarServicio(id: number, data: ServicioRequest): Observable<Servicio> {
    return this.http.put<Servicio>(`${this.API}/servicios/${id}`, data);
  }
  eliminarServicio(id: number): Observable<MensajeResponse> {
    return this.http.delete<MensajeResponse>(`${this.API}/servicios/${id}`);
  }

  // ----- Reservas del cliente -----
  crearReserva(data: ReservaRequest): Observable<Reserva> {
    return this.http.post<Reserva>(`${this.API}/reservas`, data);
  }
  misReservas(): Observable<Reserva[]> {
    return this.http.get<Reserva[]>(`${this.API}/reservas/mis-reservas`);
  }
  cancelarReserva(id: number): Observable<Reserva> {
    return this.http.put<Reserva>(`${this.API}/reservas/${id}/cancelar`, {});
  }

  // ----- Panel del administrador -----
  reservasAdmin(estado?: EstadoReserva): Observable<Reserva[]> {
    let params = new HttpParams();
    if (estado) params = params.set('estado', estado);
    return this.http.get<Reserva[]>(`${this.API}/admin/reservas`, { params });
  }
  marcarAtendida(id: number): Observable<Reserva> {
    return this.http.put<Reserva>(`${this.API}/admin/reservas/${id}/atendida`, {});
  }
  listarUsuarios(): Observable<Usuario[]> {
    return this.http.get<Usuario[]>(`${this.API}/admin/usuarios`);
  }
  actualizarUsuario(id: number, data: UsuarioUpdateRequest): Observable<Usuario> {
    return this.http.put<Usuario>(`${this.API}/admin/usuarios/${id}`, data);
  }
  eliminarUsuario(id: number): Observable<MensajeResponse> {
    return this.http.delete<MensajeResponse>(`${this.API}/admin/usuarios/${id}`);
  }
}
