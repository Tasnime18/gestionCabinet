import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { MedecinService, AppointmentResponse, PatientFile } from '../../services/medecin.service';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-medecin-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './medecin-dashboard.component.html',
  styleUrl: './medecin-dashboard.component.css'
})
export class MedecinDashboardComponent implements OnInit {
  appointments: AppointmentResponse[] = [];
  loading = false;
  error = '';
  success = '';
  activeTab = 'pending'; // pending, accepted, all, patients

  // Statistics
  stats = {
    total: 0,
    pending: 0,
    accepted: 0,
    rejected: 0,
    completed: 0
  };

  // Patient Files
  patientFiles: PatientFile[] = [];
  selectedPatientFile: PatientFile | null = null;
  showPatientFileForm = false;
  isEditingPatientFile = false;

  // Patient file form
  patientFileForm: PatientFile = this.getEmptyPatientFileForm();

  constructor(
    public authService: AuthService,
    private medecinService: MedecinService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadAppointments();
  }

  loadAppointments(): void {
    this.loading = true;
    this.error = '';
    
    this.medecinService.getMedecinAppointments().subscribe({
      next: (data) => {
        console.log('Appointments received:', data);
        this.appointments = data.sort((a, b) => 
          new Date(b.appointmentDate).getTime() - new Date(a.appointmentDate).getTime()
        );
        this.updateStats();
        this.loading = false;
      },
      error: (err) => {
        console.error('Error loading appointments:', err);
        this.error = 'Erreur lors du chargement des rendez-vous';
        this.loading = false;
      }
    });
  }

  loadPatientFiles(): void {
    this.loading = true;
    this.error = '';
    
    this.medecinService.getAllPatientFiles().subscribe({
      next: (data) => {
        console.log('Patient files received:', data);
        this.patientFiles = data;
        this.loading = false;
      },
      error: (err) => {
        console.error('Error loading patient files:', err);
        this.error = 'Erreur lors du chargement des dossiers patients';
        this.loading = false;
      }
    });
  }

  updateStats(): void {
    this.stats.total = this.appointments.length;
    this.stats.pending = this.appointments.filter(a => a.status === 'SCHEDULED').length;
    this.stats.accepted = this.appointments.filter(a => a.status === 'CONFIRMED').length;
    this.stats.rejected = this.appointments.filter(a => a.status === 'REJECTED').length;
    this.stats.completed = this.appointments.filter(a => a.status === 'COMPLETED').length;
  }

  setActiveTab(tab: string): void {
    this.activeTab = tab;
    this.error = '';
    this.success = '';
    
    if (tab === 'patients') {
      this.loadPatientFiles();
    }
  }

  getFilteredAppointments(): AppointmentResponse[] {
    switch (this.activeTab) {
      case 'pending':
        return this.appointments.filter(a => a.status === 'SCHEDULED');
      case 'accepted':
        return this.appointments.filter(a => a.status === 'CONFIRMED');
      case 'completed':
        return this.appointments.filter(a => a.status === 'COMPLETED');
      case 'rejected':
        return this.appointments.filter(a => a.status === 'REJECTED');
      default:
        return this.appointments;
    }
  }

  acceptAppointment(appointment: AppointmentResponse): void {
    if (!confirm(`Êtes-vous sûr de vouloir accepter le rendez-vous de ${appointment.patientUsername}?`)) {
      return;
    }

    this.loading = true;
    this.medecinService.acceptAppointment(appointment.id).subscribe({
      next: (updated) => {
        this.success = 'Rendez-vous accepté avec succès!';
        this.updateAppointmentInList(updated);
        this.updateStats();
        this.loading = false;
        
        // Check if patient file exists and prompt to create if not
        this.checkAndPromptForPatientFile(appointment.patientId, appointment.patientUsername);
      },
      error: (err) => {
        this.error = err.error?.message || 'Erreur lors de l\'acceptation du rendez-vous';
        this.loading = false;
      }
    });
  }

  checkAndPromptForPatientFile(patientId: number, patientUsername: string): void {
    this.medecinService.patientFileExists(patientId).subscribe({
      next: (exists) => {
        if (!exists) {
          if (confirm(`Le patient ${patientUsername} n'a pas encore de dossier. Voulez-vous en créer un maintenant?`)) {
            this.openCreatePatientFileForm(patientId);
          }
        }
      },
      error: (err) => {
        console.error('Error checking patient file existence:', err);
      }
    });
  }

  rejectAppointment(appointment: AppointmentResponse): void {
    if (!confirm(`Êtes-vous sûr de vouloir refuser le rendez-vous de ${appointment.patientUsername}?`)) {
      return;
    }

    this.loading = true;
    this.medecinService.rejectAppointment(appointment.id).subscribe({
      next: (updated) => {
        this.success = 'Rendez-vous refusé avec succès!';
        this.updateAppointmentInList(updated);
        this.updateStats();
        this.loading = false;
      },
      error: (err) => {
        this.error = err.error?.message || 'Erreur lors du refus du rendez-vous';
        this.loading = false;
      }
    });
  }

  completeAppointment(appointment: AppointmentResponse): void {
    if (!confirm(`Marquer le rendez-vous de ${appointment.patientUsername} comme terminé?`)) {
      return;
    }

    this.loading = true;
    this.medecinService.completeAppointment(appointment.id).subscribe({
      next: (updated) => {
        this.success = 'Rendez-vous marqué comme terminé!';
        this.updateAppointmentInList(updated);
        this.updateStats();
        this.loading = false;
      },
      error: (err) => {
        this.error = err.error?.message || 'Erreur lors de la completion du rendez-vous';
        this.loading = false;
      }
    });
  }

  updateAppointmentInList(updated: AppointmentResponse): void {
    const index = this.appointments.findIndex(a => a.id === updated.id);
    if (index !== -1) {
      this.appointments[index] = updated;
    }
  }

  formatDate(dateString: string): string {
    const date = new Date(dateString);
    return date.toLocaleDateString('fr-FR', {
      weekday: 'long',
      year: 'numeric',
      month: 'long',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  }

  getStatusClass(status: string): string {
    switch (status) {
      case 'SCHEDULED': return 'status-pending';
      case 'CONFIRMED': return 'status-confirmed';
      case 'REJECTED': return 'status-rejected';
      case 'COMPLETED': return 'status-completed';
      case 'CANCELLED': return 'status-cancelled';
      default: return '';
    }
  }

  getStatusLabel(status: string): string {
    switch (status) {
      case 'SCHEDULED': return 'En attente';
      case 'CONFIRMED': return 'Confirmé';
      case 'REJECTED': return 'Refusé';
      case 'COMPLETED': return 'Terminé';
      case 'CANCELLED': return 'Annulé';
      default: return status;
    }
  }

  isPending(status: string): boolean {
    return status === 'SCHEDULED';
  }

  isConfirmed(status: string): boolean {
    return status === 'CONFIRMED';
  }

  // Patient File Management
  openCreatePatientFileForm(patientId: number): void {
    this.patientFileForm = this.getEmptyPatientFileForm();
    this.patientFileForm.patientId = patientId;
    this.isEditingPatientFile = false;
    this.showPatientFileForm = true;
    this.error = '';
    this.success = '';
  }

  editPatientFile(patientFile: PatientFile): void {
    this.patientFileForm = { ...patientFile };
    this.isEditingPatientFile = true;
    this.showPatientFileForm = true;
    this.error = '';
    this.success = '';
  }

  closePatientFileForm(): void {
    this.showPatientFileForm = false;
    this.patientFileForm = this.getEmptyPatientFileForm();
  }

  getEmptyPatientFileForm(): PatientFile {
    return {
      patientId: 0,
      firstName: '',
      lastName: '',
      dateOfBirth: '',
      gender: '',
      phone: '',
      email: '',
      address: '',
      bloodType: '',
      allergies: '',
      currentMedications: '',
      medicalHistory: '',
      familyMedicalHistory: '',
      dentalHistory: '',
      currentDentalIssues: '',
      previousTreatments: '',
      dentalNotes: '',
      consultationNotes: ''
    };
  }

  savePatientFile(): void {
    if (!this.patientFileForm.patientId) {
      this.error = 'ID du patient requis';
      return;
    }

    this.loading = true;

    if (this.isEditingPatientFile) {
      this.medecinService.updatePatientFile(this.patientFileForm.patientId, this.patientFileForm).subscribe({
        next: () => {
          this.success = 'Dossier patient mis à jour avec succès!';
          this.showPatientFileForm = false;
          this.loadPatientFiles();
          this.loading = false;
        },
        error: (err) => {
          this.error = err.error?.message || 'Erreur lors de la mise à jour du dossier patient';
          this.loading = false;
        }
      });
    } else {
      this.medecinService.createPatientFile(this.patientFileForm).subscribe({
        next: () => {
          this.success = 'Dossier patient créé avec succès!';
          this.showPatientFileForm = false;
          this.loadPatientFiles();
          this.loading = false;
        },
        error: (err) => {
          this.error = err.error?.message || 'Erreur lors de la création du dossier patient';
          this.loading = false;
        }
      });
    }
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/auth']);
  }

  goToAuth(): void {
    this.router.navigate(['/auth']);
  }
}
