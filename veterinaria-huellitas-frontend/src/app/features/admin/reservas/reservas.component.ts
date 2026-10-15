import { Component, OnInit, inject, signal } from '@angular/core';
import { ApiService } from '../../../core/services/api.service';
import { ToastService } from '../../../core/services/toast.service';
import { EstadoReserva, Reserva } from '../../../core/models/api.models';
import { formatFecha, formatHora } from '../../../core/utils/fechas';

type Filtro = 'TODAS' | EstadoReserva;

/** El administrador supervisa las reservas y las marca como atendidas. NO puede cancelarlas. */
@Component({
  selector: 'app-admin-reservas',
  standalone: true,
  templateUrl: './reservas.component.html',
  styleUrl: './reservas.component.scss',
})
export class AdminReservasComponent implements OnInit {
  private api = inject(ApiService);
  private toast = inject(ToastService);

  cargando = signal(true);
  reservas = signal<Reserva[]>([]);
  filtro = signal<Filtro>('TODAS');
  procesando = signal<number | null>(null);

  filtros: { valor: Filtro; texto: string }[] = [
    { valor: 'TODAS', texto: 'Todas' },
    { valor: 'CONFIRMADA', texto: 'Pendientes' },
    { valor: 'ATENDIDA', texto: 'Atendidas' },
    { valor: 'CANCELADA', texto: 'Canceladas' },
  ];

  fecha = formatFecha;
  hora = formatHora;

  ngOnInit(): void {
    this.cargar();
  }

  elegir(f: Filtro): void {
    this.filtro.set(f);
    this.cargar();
  }

  cargar(): void {
    this.cargando.set(true);
    const f = this.filtro();
    this.api.reservasAdmin(f === 'TODAS' ? undefined : f).subscribe({
      next: (r) => { this.reservas.set(r); this.cargando.set(false); },
      error: (err) => { this.cargando.set(false); this.toast.show(err.error?.mensaje || 'No se pudieron cargar las reservas'); },
    });
  }

  marcarAtendida(r: Reserva): void {
    this.procesando.set(r.id);
    this.api.marcarAtendida(r.id).subscribe({
      next: () => { this.procesando.set(null); this.toast.show('Reserva marcada como atendida'); this.cargar(); },
      error: (err) => { this.procesando.set(null); this.toast.show(err.error?.mensaje || 'No se pudo actualizar la reserva'); },
    });
  }
}
