import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

// El usuario inicia sesión con su correo (ya no existe "username")
export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegistroRequest {
  nombreCompleto: string;
  email: string;
  password: string;
}

export interface AuthResponse {
  token: string;
  email: string;
  nombreCompleto: string;
  rol: string;
}

export interface MensajeResponse {
  mensaje: string;
}

@Injectable({ providedIn: 'root' })
export class AuthService {
  // URL base de tu backend Spring Boot (puerto 8085, ver application.properties)
  private readonly API_BASE = 'http://localhost:8085';

  constructor(private http: HttpClient) {}

  login(data: LoginRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.API_BASE}/api/auth/login`, data);
  }

  registro(data: RegistroRequest): Observable<MensajeResponse> {
    return this.http.post<MensajeResponse>(`${this.API_BASE}/api/auth/registro`, data);
  }

  saveSession(auth: AuthResponse): void {
    localStorage.setItem('token', auth.token);
    localStorage.setItem('email', auth.email);
    localStorage.setItem('nombreCompleto', auth.nombreCompleto);
    localStorage.setItem('rol', auth.rol);
  }

  getToken(): string | null {
    return localStorage.getItem('token');
  }

  getRol(): string | null {
    return localStorage.getItem('rol');
  }

  getNombre(): string {
    return localStorage.getItem('nombreCompleto') ?? '';
  }

  getEmail(): string {
    return localStorage.getItem('email') ?? '';
  }

  /** Ruta de inicio según el rol: el administrador tiene su propio panel. */
  homePath(): string {
    return this.getRol() === 'ADMIN' ? '/admin/inicio' : '/cliente/inicio';
  }

  logout(): void {
    localStorage.removeItem('token');
    localStorage.removeItem('email');
    localStorage.removeItem('nombreCompleto');
    localStorage.removeItem('rol');
  }
}
