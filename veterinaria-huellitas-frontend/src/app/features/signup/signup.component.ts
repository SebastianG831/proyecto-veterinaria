import { Component, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { ToastService } from '../../core/services/toast.service';

@Component({
  selector: 'app-signup',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './signup.component.html',
  styleUrl: './signup.component.scss',
})
export class SignupComponent {
  private fb = inject(FormBuilder);
  private auth = inject(AuthService);
  private toast = inject(ToastService);
  private router = inject(Router);

  loading = false;

  form = this.fb.group({
    nombreCompleto: ['', Validators.required],
    username: ['', [Validators.required, Validators.minLength(4)]],
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(6)]],
    terms: [false, Validators.requiredTrue],
  });

  get f() {
    return this.form.controls;
  }

  onSubmit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      if (this.f.terms.invalid) {
        this.toast.show('Debes aceptar los términos y condiciones');
      }
      return;
    }

    this.loading = true;
    const { nombreCompleto, username, email, password } = this.form.getRawValue();

    this.auth
      .registro({
        nombreCompleto: nombreCompleto!,
        username: username!,
        email: email!,
        password: password!,
      })
      .subscribe({
        next: (data) => {
          this.loading = false;
          this.toast.show(data.mensaje || '¡Cuenta creada con éxito!');
          this.form.reset();
          this.router.navigate(['/login']);
        },
        error: (err) => {
          this.loading = false;
          this.toast.show(err.error?.mensaje || 'No se pudo crear la cuenta');
        },
      });
  }
}
