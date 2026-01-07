import { inject } from '@angular/core';
import { Router, CanActivateFn } from '@angular/router';
import { AuthService } from '../services/auth.service';

export const authGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  if (!authService.isLoggedIn()) {
    authService.logout();
    router.navigate(['/auth']);
    return false;
  }

  const expectedRole = route.data?.['role'];
  const userRole = authService.getUserRole();

  // If a specific role is required, check it
  if (expectedRole && userRole !== expectedRole) {
    // Redirect to appropriate dashboard based on actual role
    if (userRole === 'MEDECIN') {
      router.navigate(['/medecin-dashboard']);
    } else if (userRole === 'PATIENT') {
      router.navigate(['/patient-dashboard']);
    } else {
      authService.logout();
      router.navigate(['/auth']);
    }
    return false;
  }

  return true;
};

export const loginGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  if (!authService.isLoggedIn()) {
    return true;
  }

  // Already logged in, redirect to appropriate dashboard
  const role = authService.getUserRole();
  if (role === 'MEDECIN') {
    router.navigate(['/medecin-dashboard']);
  } else if (role === 'PATIENT') {
    router.navigate(['/patient-dashboard']);
  } else {
    router.navigate(['/auth']);
  }
  return false;
};
