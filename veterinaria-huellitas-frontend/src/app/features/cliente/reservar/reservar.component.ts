import { Component, OnInit, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { ApiService } from '../../../core/services/api.service';
import { ToastService } from '../../../core/services/toast.service';
import { Servicio } from '../../../core/models/api.models';
import { ahoraParaInput, formatFecha, formatHora } from '../../../core/utils/fechas';

@Component({
  selector: 'app-cliente-reservar',
  standalone: true,
  imports: [ReactiveFormsModule],
  templateUrl: './reservar.component.html',
  styleUrl: './reservar.component.scss',
})
export class ClienteReservarComponent implements OnInit {
  private fb = inject(FormBuilder);
  private api = inject(ApiService);
  private toast = inject(ToastService);
  private router = inject(Router);
  private route = inject(ActivatedRoute);

  servicios = signal<Servicio[]>([]);
  mascotasConocidas = signal<string[]>([]);
  enviando = signal(false);
  errorServidor = signal('');
  minFecha = ahoraParaInput();

  form = this.fb.group({
    servicioId: [null as number | null, Validators.required],
    nombreMascota: ['', [Validators.required, Validators.maxLength(100)]],
    fechaHora: ['', Validators.required],
    observaciones: ['', Validators.maxLength(500)],
  });

  get f() {
    return this.form.controls;
  }

  get servicioElegido(): Servicio | undefined {
    return this.servicios().find((s) => s.id === Number(this.f.servicioId.value));
  }

  ngOnInit(): void {
    this.api.listarServicios().subscribe({
      next: (lista) => {
        this.servicios.set(lista);
        const id = Number(this.route.snapshot.queryParamMap.get('servicio'));
        if (id && lista.some((s) => s.id === id)) this.f.servicioId.setValue(id);
      },
      error: () => this.toast.show('No se pudieron cargar los servicios'),
    });

    // Sugiere los nombres de mascotas de reservas anteriores
    this.api.misReservas().subscribe({
      next: (r) => this.mascotasConocidas.set([...new Set(r.map((x) => x.nombreMascota.trim()))]),
    });

    const mascota = this.route.snapshot.queryParamMap.get('mascota');
    if (mascota) this.f.nombreMascota.setValue(mascota);
  }

  enviar(): void {
    this.errorServidor.set('');
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    const v = this.form.getRawValue();
    this.enviando.set(true);

    this.api.crearReserva({
      servicioId: Number(v.servicioId),
      nombreMascota: v.nombreMascota!.trim(),
      fechaHora: `${v.fechaHora}:00`, // el input da "2026-10-20T10:00"; el backend espera segundos
      observaciones: v.observaciones?.trim() || undefined,
    }).subscribe({
      next: (r) => {
        this.enviando.set(false);
        this.toast.show(`¡Cita confirmada! ${formatFecha(r.fechaHora)} a las ${formatHora(r.fechaHora)}`);
        this.router.navigate(['/cliente/citas']);
      },
      error: (err) => {
        this.enviando.set(false);
        // Mensajes del backend: horario de atención, fecha futura, cruce con otra reserva (409)...
        const mensaje = err.error?.mensaje
          || (err.status === 409 ? 'Ese horario ya está ocupado. Elige otra hora' : 'No se pudo crear la reserva');
        this.errorServidor.set(mensaje);
        this.toast.show(mensaje);
      },
    });
  }
}
