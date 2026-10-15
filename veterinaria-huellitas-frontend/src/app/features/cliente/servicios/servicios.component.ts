import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { Router } from '@angular/router';
import { ApiService } from '../../../core/services/api.service';
import { ToastService } from '../../../core/services/toast.service';
import { Servicio } from '../../../core/models/api.models';

@Component({
  selector: 'app-cliente-servicios',
  standalone: true,
  templateUrl: './servicios.component.html',
  styleUrl: './servicios.component.scss',
})
export class ClienteServiciosComponent implements OnInit {
  private api = inject(ApiService);
  private toast = inject(ToastService);
  private router = inject(Router);

  cargando = signal(true);
  servicios = signal<Servicio[]>([]);
  busqueda = signal('');

  visibles = computed(() => {
    const q = this.busqueda().trim().toLowerCase();
    return this.servicios().filter((s) => !q || s.nombre.toLowerCase().includes(q));
  });

  ngOnInit(): void {
    this.api.listarServicios().subscribe({
      next: (lista) => { this.servicios.set(lista); this.cargando.set(false); },
      error: () => { this.cargando.set(false); this.toast.show('No se pudieron cargar los servicios'); },
    });
  }

  reservar(s: Servicio): void {
    this.router.navigate(['/cliente/reservar'], { queryParams: { servicio: s.id } });
  }
}
