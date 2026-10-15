import { Component, inject } from '@angular/core';
import { Router, RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { ToastService } from '../../core/services/toast.service';

interface NavItem {
  label: string;
  path: string;
  icon: 'home' | 'paw' | 'calendar' | 'plus' | 'services' | 'list' | 'users';
}

const NAV_CLIENTE: NavItem[] = [
  { label: 'Inicio', path: '/cliente/inicio', icon: 'home' },
  { label: 'Mis mascotas', path: '/cliente/mascotas', icon: 'paw' },
  { label: 'Mis citas', path: '/cliente/citas', icon: 'calendar' },
  { label: 'Reservar cita', path: '/cliente/reservar', icon: 'plus' },
  { label: 'Servicios', path: '/cliente/servicios', icon: 'services' },
];

const NAV_ADMIN: NavItem[] = [
  { label: 'Inicio', path: '/admin/inicio', icon: 'home' },
  { label: 'Reservas', path: '/admin/reservas', icon: 'calendar' },
  { label: 'Servicios', path: '/admin/servicios', icon: 'services' },
  { label: 'Usuarios', path: '/admin/usuarios', icon: 'users' },
];

/** Estructura común del panel: menú lateral izquierdo + contenido de la página elegida. */
@Component({
  selector: 'app-shell',
  standalone: true,
  imports: [RouterOutlet, RouterLink, RouterLinkActive],
  templateUrl: './shell.component.html',
  styleUrl: './shell.component.scss',
})
export class ShellComponent {
  private auth = inject(AuthService);
  private router = inject(Router);
  private toast = inject(ToastService);

  esAdmin = this.auth.getRol() === 'ADMIN';
  items = this.esAdmin ? NAV_ADMIN : NAV_CLIENTE;
  nombre = this.auth.getNombre();
  rolEtiqueta = this.esAdmin ? 'Administrador' : 'Cliente';

  get inicial(): string {
    return (this.nombre || '?').trim().charAt(0).toUpperCase();
  }

  cerrarSesion(): void {
    this.auth.logout();
    this.toast.show('Sesión cerrada');
    this.router.navigate(['/login']);
  }
}
