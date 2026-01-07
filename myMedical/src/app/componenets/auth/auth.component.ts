import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService, LoginRequest } from '../../services/auth.service';

@Component({
  selector: 'app-auth',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './auth.component.html',
  styleUrl: './auth.component.css'
})
export class AuthComponent {
  loginRequest: LoginRequest = {
    username: '',
    password: ''
  };
  errorMessage: string = '';
  isLoading: boolean = false;

  constructor(
    public authService: AuthService,
    private router: Router
  ) {}

  onSubmit(): void {
    if (!this.loginRequest.username || !this.loginRequest.password) {
      this.errorMessage = 'Please enter username and password';
      return;
    }

    this.isLoading = true;
    this.errorMessage = '';

    this.authService.login(this.loginRequest).subscribe({
      next: (response) => {
        console.log('Login response:', response);
        this.isLoading = false;
        this.navigateBasedOnRole();
      },
      error: (error) => {
        this.isLoading = false;
        this.errorMessage = 'Invalid username or password';
        console.error('Login error:', error);
      }
    });
  }

  private navigateBasedOnRole(): void {
    const role = this.authService.getUserRole();
    console.log('User role:', role);
    if (role === 'MEDECIN') {
      this.router.navigate(['/medecin-dashboard']);
    } else if (role === 'PATIENT') {
      this.router.navigate(['/patient-dashboard']);
    } else {
      console.log('No valid role found, staying on auth page');
    }
  }
}
