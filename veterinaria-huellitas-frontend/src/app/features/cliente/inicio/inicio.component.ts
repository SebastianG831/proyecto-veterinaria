import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { ApiService } from '../../../core/services/api.service';
import { AuthService } from '../../../core/services/auth.service';
import { Reserva } from '../../../core/models/api.models';
import { esProxima, formatFecha, formatHora } from '../../../core/utils/fechas';

@Component({
  selector: 'app-cliente-inicio',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './inicio.component.html',
  styleUrl: './inicio.component.scss',
})
export class ClienteInicioComponent implements OnInit {
  private api = inject(ApiService);
  nombre = inject(AuthService).getNombre().split(' ')[0];

  reservas = signal<Reserva[]>([]);
  cargando = signal(true);

  proximas = computed(() =>
    this.reservas().filter((r) => esProxima(r)).sort((a, b) => a.fechaHora.localeCompare(b.fechaHora)));
  proxima = computed(() => this.proximas()[0]);
  atendidas = computed(() => this.reservas().filter((r) => r.estado === 'ATENDIDA').length);
  mascotas = computed(() => new Set(this.reservas().map((r) => r.nombreMascota.trim().toLowerCase())).size);

  fecha = formatFecha;
  hora = formatHora;

  ngOnInit(): void {
    this.api.misReservas().subscribe({
      next: (r) => { this.reservas.set(r); this.cargando.set(false); },
      error: () => this.cargando.set(false),
    });
  }
}
