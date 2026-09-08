import { Component, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { ToastService } from '../../core/services/toast.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss',
})
export class LoginComponent {
  private fb = inject(FormBuilder);
  private auth = inject(AuthService);
  private toast = inject(ToastService);

  loading = false;

  form = this.fb.group({
    username: ['', Validators.required],
    password: ['', Validators.required],
  });

  get f() {
    return this.form.controls;
  }

  onSubmit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.loading = true;
    const { username, password } = this.form.getRawValue();

    this.auth.login({ username: username!, password: password! }).subscribe({
      next: (data) => {
        this.auth.saveSession(data);
        this.toast.show(`¡Bienvenido, ${data.username}!`);
        this.loading = false;
        // Aquí puedes redirigir cuando tengas la siguiente pantalla, ej:
        // this.router.navigate(['/dashboard']);
      },
      error: (err) => {
        this.loading = false;
        if (err.status === 401 || err.status === 403) {
          this.toast.show('Usuario o contraseña incorrectos');
        } else {
          this.toast.show(err.error?.mensaje || 'No se pudo iniciar sesión');
        }
      },
    });
  }
}
