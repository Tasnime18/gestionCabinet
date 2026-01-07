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

export interface AppointmentRequest {
  appointmentDate: string;
  reason?: string;
}

export interface RescheduleRequest {
  newAppointmentDate: string;
  reason?: string;
}

export interface PatientFile {
  id?: number;
  patientId?: number;
  patientUsername?: string;
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
export class PatientService {
  private apiUrl = 'http://localhost:8080/appointments';
  private patientFilesUrl = 'http://localhost:8080/patient-files';

  constructor(private http: HttpClient) {}

  private getHeaders(): HttpHeaders {
    const token = localStorage.getItem('token');
    console.log('Token from localStorage:', token ? 'present' : 'null');
    if (!token) {
      console.error('No token found in localStorage');
      return new HttpHeaders({
        'Content-Type': 'application/json'
      });
    }
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    });
    console.log('Authorization header:', headers.get('Authorization'));
    return headers;
  }

  getAllAppointments(): Observable<Appointment[]> {
    return this.http.get<Appointment[]>(this.apiUrl, { headers: this.getHeaders() });
  }

  getAppointmentById(id: number): Observable<Appointment> {
    return this.http.get<Appointment>(`${this.apiUrl}/${id}`, { headers: this.getHeaders() });
  }

  createAppointment(request: AppointmentRequest): Observable<Appointment> {
    return this.http.post<Appointment>(this.apiUrl, request, { headers: this.getHeaders() });
  }

  cancelAppointment(id: number): Observable<Appointment> {
    return this.http.delete<Appointment>(`${this.apiUrl}/${id}`, { headers: this.getHeaders() });
  }

  rescheduleAppointment(id: number, request: RescheduleRequest): Observable<Appointment> {
    return this.http.put<Appointment>(`${this.apiUrl}/${id}/reschedule`, request, { headers: this.getHeaders() });
  }

  // Patient File methods
  getMyFile(): Observable<PatientFile> {
    console.log('Fetching my patient file...');
    return this.http.get<PatientFile>(this.patientFilesUrl + '/my-file', { headers: this.getHeaders() });
  }
}
