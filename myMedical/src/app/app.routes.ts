import { Routes } from '@angular/router';
import { AuthComponent } from './componenets/auth/auth.component';
import { MedecinDashboardComponent } from './componenets/medecin-dashboard/medecin-dashboard.component';
import { PatientDashboardComponent } from './componenets/patient-dashboard/patient-dashboard.component';
import { authGuard, loginGuard } from './guards/auth.guard';

export const routes: Routes = [
  { path: '', redirectTo: '/auth', pathMatch: 'full' },
  { path: 'auth', component: AuthComponent, canActivate: [loginGuard] },
  { 
    path: 'medecin-dashboard', 
    component: MedecinDashboardComponent, 
    canActivate: [authGuard],
    data: { role: 'MEDECIN' }
  },
  { 
    path: 'patient-dashboard', 
    component: PatientDashboardComponent, 
    canActivate: [authGuard],
    data: { role: 'PATIENT' }
  },
  { path: '**', redirectTo: '/auth' }
];
