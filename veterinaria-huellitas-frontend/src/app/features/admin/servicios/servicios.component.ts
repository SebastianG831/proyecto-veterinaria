import { Component, OnInit, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ApiService } from '../../../core/services/api.service';
import { ToastService } from '../../../core/services/toast.service';
import { Servicio } from '../../../core/models/api.models';

@Component({
  selector: 'app-admin-servicios',
  standalone: true,
  imports: [ReactiveFormsModule],
  templateUrl: './servicios.component.html',
  styleUrl: './servicios.component.scss',
})
export class AdminServiciosComponent implements OnInit {
  private fb = inject(FormBuilder);
  private api = inject(ApiService);
  private toast = inject(ToastService);

  cargando = signal(true);
  servicios = signal<Servicio[]>([]);
  editando = signal<Servicio | 'nuevo' | null>(null); // null = modal cerrado
  aEliminar = signal<Servicio | null>(null);
  guardando = signal(false);

  form = this.fb.group({
    nombre: ['', [Validators.required, Validators.maxLength(100)]],
    descripcion: ['', Validators.maxLength(500)],
    precio: [null as number | null, [Validators.required, Validators.min(0.01)]],
    duracionMinutos: [null as number | null, [Validators.required, Validators.min(5), Validators.max(480)]],
  });

  get f() {
    return this.form.controls;
  }

  ngOnInit(): void {
    this.cargar();
  }

  cargar(): void {
    this.api.listarServicios().subscribe({
      next: (l) => { this.servicios.set(l); this.cargando.set(false); },
      error: (err) => { this.cargando.set(false); this.toast.show(err.error?.mensaje || 'No se pudieron cargar los servicios'); },
    });
  }

  abrirNuevo(): void {
    this.form.reset({ nombre: '', descripcion: '', precio: null, duracionMinutos: null });
    this.editando.set('nuevo');
  }

  abrirEditar(s: Servicio): void {
    this.form.reset({ nombre: s.nombre, descripcion: s.descripcion ?? '', precio: s.precio, duracionMinutos: s.duracionMinutos });
    this.editando.set(s);
  }

  cerrar(): void {
    this.editando.set(null);
  }

  guardar(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    const v = this.form.getRawValue();
    const datos = {
      nombre: v.nombre!.trim(),
      descripcion: (v.descripcion ?? '').trim(),
      precio: Number(v.precio),
      duracionMinutos: Number(v.duracionMinutos),
      activo: true,
    };
    const actual = this.editando();
    this.guardando.set(true);
    const peticion = actual && actual !== 'nuevo'
      ? this.api.actualizarServicio(actual.id, datos)
      : this.api.crearServicio(datos);

    peticion.subscribe({
      next: () => {
        this.guardando.set(false);
        this.toast.show(actual === 'nuevo' ? 'Servicio creado' : 'Servicio actualizado');
        this.cerrar();
        this.cargar();
      },
      error: (err) => {
        this.guardando.set(false);
        this.toast.show(err.error?.mensaje || 'No se pudo guardar el servicio');
      },
    });
  }

  eliminar(): void {
    const s = this.aEliminar();
    if (!s) return;
    this.api.eliminarServicio(s.id).subscribe({
      next: (r) => { this.toast.show(r.mensaje || 'Servicio desactivado'); this.aEliminar.set(null); this.cargar(); },
      error: (err) => { this.toast.show(err.error?.mensaje || 'No se pudo eliminar'); this.aEliminar.set(null); },
    });
  }
}
