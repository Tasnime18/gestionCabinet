import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Appointment {
  id: number;
  patientId: number;
  patientUsername: string;
  medecinId: number;
  medecinUsername: string;
  appointmentDate: string;
  reason: string;
  status: string;
  createdAt: string;
}

export interface AppointmentResponse {
  id: number;
  patientId: number;
  patientUsername: string;
  medecinId: number;
  medecinUsername: string;
  appointmentDate: string;
  reason: string;
  status: 'SCHEDULED' | 'CONFIRMED' | 'REJECTED' | 'CANCELLED' | 'COMPLETED' | 'RESCHEDULED';
  createdAt: string;
}

export interface PatientFile {
  id?: number;
  patientId: number;
  firstName: string;
  lastName: string;
  dateOfBirth: string;
  gender: string;
  phone: string;
  email: string;
  address: string;
  bloodType: string;
  allergies: string;
  currentMedications: string;
  medicalHistory: string;
  familyMedicalHistory: string;
  dentalHistory: string;
  currentDentalIssues: string;
  previousTreatments: string;
  dentalNotes: string;
  consultationNotes: string;
}

@Injectable({
  providedIn: 'root'
})
export class MedecinService {
  private apiUrl = 'http://localhost:8080';
  private appointmentsUrl = 'http://localhost:8080/appointments';
  private patientFilesUrl = 'http://localhost:8080/patient-files';

  constructor(private http: HttpClient) {}

  private getHeaders(): HttpHeaders {
    const token = localStorage.getItem('token');
    if (!token) {
      console.error('No token found in localStorage');
      return new HttpHeaders({
        'Content-Type': 'application/json'
      });
    }
    return new HttpHeaders({
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    });
  }

  // Appointment methods
  getMedecinAppointments(): Observable<AppointmentResponse[]> {
    console.log('Fetching medecin appointments...');
    return this.http.get<AppointmentResponse[]>(this.appointmentsUrl + '/medecin', { headers: this.getHeaders() });
  }

  acceptAppointment(id: number): Observable<AppointmentResponse> {
    console.log('Accepting appointment:', id);
    return this.http.put<AppointmentResponse>(`${this.appointmentsUrl}/${id}/accept`, {}, { headers: this.getHeaders() });
  }

  rejectAppointment(id: number): Observable<AppointmentResponse> {
    console.log('Rejecting appointment:', id);
    return this.http.put<AppointmentResponse>(`${this.appointmentsUrl}/${id}/reject`, {}, { headers: this.getHeaders() });
  }

  getAppointmentById(id: number): Observable<AppointmentResponse> {
    return this.http.get<AppointmentResponse>(`${this.appointmentsUrl}/${id}`, { headers: this.getHeaders() });
  }

  completeAppointment(id: number): Observable<AppointmentResponse> {
    console.log('Completing appointment:', id);
    return this.http.put<AppointmentResponse>(`${this.appointmentsUrl}/${id}/complete`, {}, { headers: this.getHeaders() });
  }

  // Patient File methods
  getAllPatientFiles(): Observable<PatientFile[]> {
    console.log('Fetching all patient files...');
    return this.http.get<PatientFile[]>(this.patientFilesUrl, { headers: this.getHeaders() });
  }

  getPatientFile(patientId: number): Observable<PatientFile> {
    console.log('Fetching patient file for patient:', patientId);
    return this.http.get<PatientFile>(`${this.patientFilesUrl}/patient/${patientId}`, { headers: this.getHeaders() });
  }

  createPatientFile(patientFile: PatientFile): Observable<PatientFile> {
    console.log('Creating patient file:', patientFile);
    return this.http.post<PatientFile>(this.patientFilesUrl, patientFile, { headers: this.getHeaders() });
  }

  updatePatientFile(patientId: number, patientFile: PatientFile): Observable<PatientFile> {
    console.log('Updating patient file for patient:', patientId);
    return this.http.put<PatientFile>(`${this.patientFilesUrl}/patient/${patientId}`, patientFile, { headers: this.getHeaders() });
  }

  patientFileExists(patientId: number): Observable<boolean> {
    return this.http.get<boolean>(`${this.patientFilesUrl}/patient/${patientId}/exists`, { headers: this.getHeaders() });
  }
}
