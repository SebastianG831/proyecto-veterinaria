import { Routes } from '@angular/router';
import { roleGuard } from './core/guards/auth.guard';
import { ShellComponent } from './layout/shell/shell.component';
import { HomeComponent } from './features/home/home.component';
import { LoginComponent } from './features/login/login.component';
import { SignupComponent } from './features/signup/signup.component';
import { ClienteInicioComponent } from './features/cliente/inicio/inicio.component';
import { ClienteMascotasComponent } from './features/cliente/mascotas/mascotas.component';
import { ClienteCitasComponent } from './features/cliente/citas/citas.component';
import { ClienteReservarComponent } from './features/cliente/reservar/reservar.component';
import { ClienteServiciosComponent } from './features/cliente/servicios/servicios.component';
import { AdminDashboardComponent } from './features/admin/dashboard/dashboard.component';
import { AdminReservasComponent } from './features/admin/reservas/reservas.component';
import { AdminServiciosComponent } from './features/admin/servicios/servicios.component';
import { AdminUsuariosComponent } from './features/admin/usuarios/usuarios.component';

export const routes: Routes = [
  { path: '', component: HomeComponent },
  { path: 'login', component: LoginComponent },
  { path: 'signup', component: SignupComponent },

  // Panel del cliente (menú lateral izquierdo)
  {
    path: 'cliente',
    component: ShellComponent,
    canActivate: [roleGuard('CLIENTE')],
    children: [
      { path: '', pathMatch: 'full', redirectTo: 'inicio' },
      { path: 'inicio', component: ClienteInicioComponent },
      { path: 'mascotas', component: ClienteMascotasComponent },
      { path: 'citas', component: ClienteCitasComponent },
      { path: 'reservar', component: ClienteReservarComponent },
      { path: 'servicios', component: ClienteServiciosComponent },
    ],
  },

  // Panel del administrador (menú lateral izquierdo)
  {
    path: 'admin',
    component: ShellComponent,
    canActivate: [roleGuard('ADMIN')],
    children: [
      { path: '', pathMatch: 'full', redirectTo: 'inicio' },
      { path: 'inicio', component: AdminDashboardComponent },
      { path: 'reservas', component: AdminReservasComponent },
      { path: 'servicios', component: AdminServiciosComponent },
      { path: 'usuarios', component: AdminUsuariosComponent },
    ],
  },

  { path: '**', redirectTo: '' },
];
