import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

/** Solo deja pasar si el usuario tiene el rol indicado; si no, lo manda a su panel o al login. */
export const roleGuard = (rol: 'ADMIN' | 'CLIENTE'): CanActivateFn => () => {
  const auth = inject(AuthService);
  const router = inject(Router);

  if (!auth.getToken()) return router.createUrlTree(['/login']);
  if (auth.getRol() === rol) return true;
  return router.createUrlTree([auth.homePath()]);
};
