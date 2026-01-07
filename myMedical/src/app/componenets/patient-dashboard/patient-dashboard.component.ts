import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { PatientService, Appointment, AppointmentRequest, RescheduleRequest, PatientFile } from '../../services/patient.service';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-patient-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './patient-dashboard.component.html',
  styleUrl: './patient-dashboard.component.css'
})
export class PatientDashboardComponent implements OnInit {
  activeTab = 'appointments';
  appointments: Appointment[] = [];
  loading = false;
  error = '';
  success = '';

  // Patient File
  myPatientFile: PatientFile | null = null;
  showPatientFileModal = false;
  hasPatientFile = false;

  // New appointment form
  showAppointmentForm = false;
  newAppointment: AppointmentRequest = {
    appointmentDate: '',
    reason: ''
  };

  // Reschedule form
  showRescheduleForm = false;
  selectedAppointment: Appointment | null = null;
  rescheduleRequest: RescheduleRequest = {
    newAppointmentDate: '',
    reason: ''
  };

  // Cabinet info
  cabinetInfo = {
    name: 'Cabinet Dentaire Smile',
    dentist: 'Dr. Jean Dupont',
    specialty: 'Chirurgie Dentaire et Orthodontie',
    address: '123 Rue de la Santé, 75001 Paris',
    phone: '01 23 45 67 89',
    email: 'contact@smiledentaire.fr',
    hours: 'Lundi - Samedi: 9h00 - 18h00',
    description: 'Notre cabinet dentaire vous accueille dans un environnement moderne et chaleureux. Nous proposons des soins dentaires de qualité pour toute la famille, de la prophylaxie à la chirurgie buccale.'
  };

  // Services
  services = [
    {
      title: 'Consultation et Diagnostic',
      icon: '🔍',
      description: 'Examen buccal complet, radiographies et diagnostic personnalisé'
    },
    {
      title: 'Soins Conservateurs',
      icon: '🦷',
      description: 'Traitement des caries, dévitalisations et soins des gencives'
    },
    {
      title: 'Orthodontie',
      icon: '✨',
      description: 'Alignement des dents avec Invisalign et appareils traditionnels'
    },
    {
      title: 'Prothèses Dentaires',
      icon: '🦷',
      description: 'Couronnes, bridges, prothèses amovibles et implants'
    },
    {
      title: 'Esthétique Dentaire',
      icon: '💎',
      description: 'Blanchiment des dents et facettes céramiques'
    },
    {
      title: 'Urgences Dentaires',
      icon: '🚨',
      description: 'Prise en charge rapide des douleurs et traumatismes dentaires'
    }
  ];

  constructor(
    public patientService: PatientService,
    public authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadAppointments();
  }

  loadAppointments(): void {
    this.loading = true;
    this.patientService.getAllAppointments().subscribe({
      next: (data) => {
        this.appointments = data.sort((a, b) => 
          new Date(b.appointmentDate).getTime() - new Date(a.appointmentDate).getTime()
        );
        this.loading = false;
      },
      error: (err) => {
        this.error = 'Erreur lors du chargement des rendez-vous';
        this.loading = false;
        console.error(err);
      }
    });
  }

  loadMyPatientFile(): void {
    this.loading = true;
    this.error = '';
    
    this.patientService.getMyFile().subscribe({
      next: (data) => {
        this.myPatientFile = data;
        this.hasPatientFile = true;
        this.showPatientFileModal = true;
        this.loading = false;
      },
      error: (err) => {
        if (err.status === 404) {
          this.hasPatientFile = false;
          this.showPatientFileModal = true;
        } else {
          this.error = 'Erreur lors du chargement de votre dossier patient';
        }
        this.loading = false;
      }
    });
  }

  setActiveTab(tab: string): void {
    this.activeTab = tab;
    this.error = '';
    this.success = '';
    
    if (tab === 'my-file') {
      this.loadMyPatientFile();
    }
  }

  openAppointmentForm(): void {
    this.showAppointmentForm = true;
    this.newAppointment = {
      appointmentDate: '',
      reason: ''
    };
    this.error = '';
    this.success = '';
  }

  closeAppointmentForm(): void {
    this.showAppointmentForm = false;
  }

  createAppointment(): void {
    if (!this.newAppointment.appointmentDate) {
      this.error = 'Veuillez sélectionner une date et heure';
      return;
    }

    if (!this.authService.isLoggedIn()) {
      this.error = 'Vous devez être connecté pour créer un rendez-vous';
      return;
    }

    this.loading = true;
    this.patientService.createAppointment(this.newAppointment).subscribe({
      next: () => {
        this.success = 'Rendez-vous créé avec succès!';
        this.showAppointmentForm = false;
        this.loadAppointments();
        this.loading = false;
      },
      error: (err) => {
        console.error('Create appointment error:', err);
        if (err.status === 403) {
          this.error = 'Accès refusé. Veuillez vous reconnecter.';
        } else {
          this.error = err.error?.message || 'Erreur lors de la création du rendez-vous';
        }
        this.loading = false;
      }
    });
  }

  openRescheduleForm(appointment: Appointment): void {
    this.selectedAppointment = appointment;
    this.showRescheduleForm = true;
    this.rescheduleRequest = {
      newAppointmentDate: '',
      reason: ''
    };
    this.error = '';
    this.success = '';
  }

  closeRescheduleForm(): void {
    this.showRescheduleForm = false;
    this.selectedAppointment = null;
  }

  rescheduleAppointment(): void {
    if (!this.rescheduleRequest.newAppointmentDate) {
      this.error = 'Veuillez sélectionner une nouvelle date et heure';
      return;
    }

    if (!this.selectedAppointment) return;

    this.loading = true;
    this.patientService.rescheduleAppointment(this.selectedAppointment.id, this.rescheduleRequest).subscribe({
      next: () => {
        this.success = 'Rendez-vous reporté avec succès!';
        this.showRescheduleForm = false;
        this.loadAppointments();
        this.loading = false;
      },
      error: (err) => {
        this.error = err.error?.message || 'Erreur lors du report du rendez-vous';
        this.loading = false;
      }
    });
  }

  cancelAppointment(appointment: Appointment): void {
    if (!confirm('Êtes-vous sûr de vouloir annuler ce rendez-vous?')) {
      return;
    }

    this.loading = true;
    this.patientService.cancelAppointment(appointment.id).subscribe({
      next: () => {
        this.success = 'Rendez-vous annulé avec succès!';
        this.loadAppointments();
        this.loading = false;
      },
      error: (err) => {
        this.error = err.error?.message || 'Erreur lors de l\'annulation';
        this.loading = false;
      }
    });
  }

  closePatientFileModal(): void {
    this.showPatientFileModal = false;
  }

  isUpcoming(dateString: string): boolean {
    return new Date(dateString) > new Date();
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

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/auth']);
  }
}
