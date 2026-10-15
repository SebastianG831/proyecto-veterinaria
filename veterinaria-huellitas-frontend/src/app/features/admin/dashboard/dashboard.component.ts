import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { forkJoin } from 'rxjs';
import { ApiService } from '../../../core/services/api.service';
import { ToastService } from '../../../core/services/toast.service';
import { Reserva, Servicio, Usuario } from '../../../core/models/api.models';
import { claveMes, esProxima, etiquetaMes, formatFecha, formatHora } from '../../../core/utils/fechas';

interface Segmento { etiqueta: string; valor: number; color: string; largo: number; desfase: number; }
interface Barra { etiqueta: string; valor: number; alto: number; }

const COLORES = { CONFIRMADA: '#6C8BE0', ATENDIDA: '#4FB58D', CANCELADA: '#E07A7A' };
const CIRCUNFERENCIA = 2 * Math.PI * 70;

@Component({
  selector: 'app-admin-dashboard',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.scss',
})
export class AdminDashboardComponent implements OnInit {
  private api = inject(ApiService);
  private toast = inject(ToastService);

  cargando = signal(true);
  reservas = signal<Reserva[]>([]);
  usuarios = signal<Usuario[]>([]);
  servicios = signal<Servicio[]>([]);

  total = computed(() => this.reservas().length);
  pendientes = computed(() => this.reservas().filter((r) => r.estado === 'CONFIRMADA').length);
  realizadas = computed(() => this.reservas().filter((r) => r.estado === 'ATENDIDA').length);
  canceladas = computed(() => this.reservas().filter((r) => r.estado === 'CANCELADA').length);
  clientes = computed(() => this.usuarios().filter((u) => u.rol === 'CLIENTE').length);

  /** Gráfica de dona: reservas por estado. */
  segmentos = computed<Segmento[]>(() => {
    const datos = [
      { etiqueta: 'Pendientes', valor: this.pendientes(), color: COLORES.CONFIRMADA },
      { etiqueta: 'Realizadas', valor: this.realizadas(), color: COLORES.ATENDIDA },
      { etiqueta: 'Canceladas', valor: this.canceladas(), color: COLORES.CANCELADA },
    ];
    const suma = datos.reduce((a, d) => a + d.valor, 0) || 1;
    let acumulado = 0;
    return datos.map((d) => {
      const largo = (d.valor / suma) * CIRCUNFERENCIA;
      const seg = { ...d, largo, desfase: -acumulado };
      acumulado += largo;
      return seg;
    });
  });
  circunferencia = CIRCUNFERENCIA;

  /** Gráfica de barras: citas por mes (últimos 6 meses). */
  meses = computed<Barra[]>(() => {
    const claves: string[] = [];
    const hoy = new Date();
    for (let i = 5; i >= 0; i--) {
      const d = new Date(hoy.getFullYear(), hoy.getMonth() - i, 1);
      claves.push(`${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}`);
    }
    const conteo = new Map(claves.map((c) => [c, 0]));
    for (const r of this.reservas()) {
      const c = claveMes(r.fechaHora);
      if (conteo.has(c)) conteo.set(c, conteo.get(c)! + 1);
    }
    const max = Math.max(1, ...conteo.values());
    return claves.map((c) => ({ etiqueta: etiquetaMes(c), valor: conteo.get(c)!, alto: (conteo.get(c)! / max) * 100 }));
  });

  /** Servicios más reservados (sin contar canceladas). */
  topServicios = computed(() => {
    const conteo = new Map<string, number>();
    for (const r of this.reservas().filter((x) => x.estado !== 'CANCELADA')) {
      conteo.set(r.servicioNombre, (conteo.get(r.servicioNombre) ?? 0) + 1);
    }
    const lista = [...conteo.entries()].sort((a, b) => b[1] - a[1]).slice(0, 5);
    const max = Math.max(1, ...lista.map((x) => x[1]));
    return lista.map(([nombre, valor]) => ({ nombre, valor, ancho: (valor / max) * 100 }));
  });

  proximas = computed(() =>
    this.reservas().filter((r) => esProxima(r)).sort((a, b) => a.fechaHora.localeCompare(b.fechaHora)).slice(0, 5));

  fecha = formatFecha;
  hora = formatHora;

  ngOnInit(): void {
    forkJoin({
      reservas: this.api.reservasAdmin(),
      usuarios: this.api.listarUsuarios(),
      servicios: this.api.listarServicios(),
    }).subscribe({
      next: (d) => {
        this.reservas.set(d.reservas);
        this.usuarios.set(d.usuarios);
        this.servicios.set(d.servicios);
        this.cargando.set(false);
      },
      error: (err) => {
        this.cargando.set(false);
        this.toast.show(err.error?.mensaje || 'No se pudo cargar el resumen');
      },
    });
  }
}
