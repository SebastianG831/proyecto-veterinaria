import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { ApiService } from '../../../core/services/api.service';
import { Reserva } from '../../../core/models/api.models';
import { esProxima, formatFecha, formatHora } from '../../../core/utils/fechas';

interface Mascota {
  nombre: string;
  visitas: number;
  ultimoServicio: string;
  ultimaVisita: string | null;
  proxima: Reserva | null;
}

/**
 * El backend guarda el nombre de la mascota dentro de cada reserva, así que
 * "Mis mascotas" se arma agrupando las reservas del cliente por ese nombre.
 */
@Component({
  selector: 'app-cliente-mascotas',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './mascotas.component.html',
  styleUrl: './mascotas.component.scss',
})
export class ClienteMascotasComponent implements OnInit {
  private api = inject(ApiService);
  private router = inject(Router);

  cargando = signal(true);
  reservas = signal<Reserva[]>([]);

  mascotas = computed<Mascota[]>(() => {
    const grupos = new Map<string, Reserva[]>();
    for (const r of this.reservas()) {
      const clave = r.nombreMascota.trim().toLowerCase();
      grupos.set(clave, [...(grupos.get(clave) ?? []), r]);
    }
    return [...grupos.values()].map((lista) => {
      const ordenadas = [...lista].sort((a, b) => b.fechaHora.localeCompare(a.fechaHora));
      const atendidas = ordenadas.filter((r) => r.estado === 'ATENDIDA');
      const proximas = lista.filter((r) => esProxima(r)).sort((a, b) => a.fechaHora.localeCompare(b.fechaHora));
      return {
        nombre: ordenadas[0].nombreMascota.trim(),
        visitas: atendidas.length,
        ultimoServicio: atendidas[0]?.servicioNombre ?? '—',
        ultimaVisita: atendidas[0]?.fechaHora ?? null,
        proxima: proximas[0] ?? null,
      };
    }).sort((a, b) => a.nombre.localeCompare(b.nombre));
  });

  fecha = formatFecha;
  hora = formatHora;

  ngOnInit(): void {
    this.api.misReservas().subscribe({
      next: (r) => { this.reservas.set(r); this.cargando.set(false); },
      error: () => this.cargando.set(false),
    });
  }

  reservarPara(m: Mascota): void {
    this.router.navigate(['/cliente/reservar'], { queryParams: { mascota: m.nombre } });
  }
}
