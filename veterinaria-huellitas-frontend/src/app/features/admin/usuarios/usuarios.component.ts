import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ApiService } from '../../../core/services/api.service';
import { AuthService } from '../../../core/services/auth.service';
import { ToastService } from '../../../core/services/toast.service';
import { Usuario } from '../../../core/models/api.models';

@Component({
  selector: 'app-admin-usuarios',
  standalone: true,
  imports: [ReactiveFormsModule],
  templateUrl: './usuarios.component.html',
  styleUrl: './usuarios.component.scss',
})
export class AdminUsuariosComponent implements OnInit {
  private fb = inject(FormBuilder);
  private api = inject(ApiService);
  private toast = inject(ToastService);
  miEmail = inject(AuthService).getEmail();

  cargando = signal(true);
  usuarios = signal<Usuario[]>([]);
  busqueda = signal('');
  editando = signal<Usuario | null>(null);
  aEliminar = signal<Usuario | null>(null);
  guardando = signal(false);

  visibles = computed(() => {
    const q = this.busqueda().trim().toLowerCase();
    return this.usuarios().filter((u) => !q || u.nombreCompleto.toLowerCase().includes(q) || u.email.toLowerCase().includes(q));
  });

  form = this.fb.group({
    nombreCompleto: ['', [Validators.required, Validators.maxLength(100)]],
    email: ['', [Validators.required, Validators.email]],
    password: ['', Validators.minLength(6)], // opcional: vacío = mantener la actual
    rol: ['CLIENTE', Validators.required],
  });

  get f() {
    return this.form.controls;
  }

  ngOnInit(): void {
    this.cargar();
  }

  cargar(): void {
    this.api.listarUsuarios().subscribe({
      next: (l) => { this.usuarios.set(l); this.cargando.set(false); },
      error: (err) => { this.cargando.set(false); this.toast.show(err.error?.mensaje || 'No se pudieron cargar los usuarios'); },
    });
  }

  abrirEditar(u: Usuario): void {
    this.form.reset({ nombreCompleto: u.nombreCompleto, email: u.email, password: '', rol: u.rol });
    this.editando.set(u);
  }

  guardar(): void {
    const u = this.editando();
    if (!u) return;
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    const v = this.form.getRawValue();
    this.guardando.set(true);
    this.api.actualizarUsuario(u.id, {
      nombreCompleto: v.nombreCompleto!.trim(),
      email: v.email!.trim(),
      password: v.password ? v.password : undefined,
      rol: v.rol!,
    }).subscribe({
      next: () => { this.guardando.set(false); this.toast.show('Usuario actualizado'); this.editando.set(null); this.cargar(); },
      error: (err) => { this.guardando.set(false); this.toast.show(err.error?.mensaje || 'No se pudo actualizar el usuario'); },
    });
  }

  eliminar(): void {
    const u = this.aEliminar();
    if (!u) return;
    this.api.eliminarUsuario(u.id).subscribe({
      next: (r) => { this.toast.show(r.mensaje || 'Usuario eliminado'); this.aEliminar.set(null); this.cargar(); },
      error: (err) => { this.toast.show(err.error?.mensaje || 'No se pudo eliminar el usuario'); this.aEliminar.set(null); },
    });
  }
}
