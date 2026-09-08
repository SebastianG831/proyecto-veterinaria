import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface LoginRequest {
  username: string;
  password: string;
}

export interface RegistroRequest {
  username: string;
  password: string;
  email: string;
  nombreCompleto: string;
}

export interface AuthResponse {
  token: string;
  username: string;
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
    localStorage.setItem('username', auth.username);
    localStorage.setItem('rol', auth.rol);
  }

  getToken(): string | null {
    return localStorage.getItem('token');
  }

  logout(): void {
    localStorage.removeItem('token');
    localStorage.removeItem('username');
    localStorage.removeItem('rol');
  }
}
