import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { ApiService } from '../../../core/services/api.service';
import { ToastService } from '../../../core/services/toast.service';
import { EstadoReserva, Reserva } from '../../../core/models/api.models';
import { formatFecha, formatHora, puedeCancelar } from '../../../core/utils/fechas';

type Filtro = 'TODAS' | EstadoReserva;

@Component({
  selector: 'app-cliente-citas',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './citas.component.html',
  styleUrl: './citas.component.scss',
})
export class ClienteCitasComponent implements OnInit {
  private api = inject(ApiService);
  private toast = inject(ToastService);

  cargando = signal(true);
  reservas = signal<Reserva[]>([]);
  filtro = signal<Filtro>('TODAS');
  aCancelar = signal<Reserva | null>(null);
  cancelando = signal(false);

  filtros: { valor: Filtro; texto: string }[] = [
    { valor: 'TODAS', texto: 'Todas' },
    { valor: 'CONFIRMADA', texto: 'Confirmadas' },
    { valor: 'ATENDIDA', texto: 'Atendidas' },
    { valor: 'CANCELADA', texto: 'Canceladas' },
  ];

  visibles = computed(() => {
    const f = this.filtro();
    return this.reservas().filter((r) => f === 'TODAS' || r.estado === f);
  });

  fecha = formatFecha;
  hora = formatHora;
  puedeCancelar = (r: Reserva) => puedeCancelar(r);

  ngOnInit(): void {
    this.cargar();
  }

  cargar(): void {
    this.api.misReservas().subscribe({
      next: (r) => { this.reservas.set(r); this.cargando.set(false); },
      error: (err) => {
        this.cargando.set(false);
        this.toast.show(err.error?.mensaje || 'No se pudieron cargar tus citas');
      },
    });
  }

  /** Texto del botón/aviso cuando ya no se puede cancelar. */
  motivoNoCancelable(r: Reserva): string {
    return r.estado === 'CONFIRMADA'
      ? 'Solo puedes cancelar con 24 horas de anticipación'
      : '';
  }

  confirmarCancelacion(): void {
    const reserva = this.aCancelar();
    if (!reserva) return;
    this.cancelando.set(true);
    this.api.cancelarReserva(reserva.id).subscribe({
      next: (actualizada) => {
        this.reservas.update((lista) => lista.map((r) => (r.id === actualizada.id ? actualizada : r)));
        this.toast.show('Cita cancelada');
        this.cancelando.set(false);
        this.aCancelar.set(null);
      },
      error: (err) => {
        // El backend explica el motivo (por ejemplo: menos de 24 horas de anticipación)
        this.toast.show(err.error?.mensaje || 'No se pudo cancelar la cita');
        this.cancelando.set(false);
        this.aCancelar.set(null);
        this.cargar();
      },
    });
  }
}
