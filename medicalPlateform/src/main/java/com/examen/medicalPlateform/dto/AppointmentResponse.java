package com.examen.medicalPlateform.dto;

import com.examen.medicalPlateform.models.Appointment;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentResponse {

    private Long id;
    private Long patientId;
    private String patientUsername;
    private Long medecinId;
    private String medecinUsername;
    private LocalDateTime appointmentDate;
    private String reason;
    private String status;
    private LocalDateTime createdAt;

    public static AppointmentResponse fromEntity(Appointment appointment) {
        AppointmentResponse response = new AppointmentResponse();
        response.setId(appointment.getId());
        response.setPatientId(appointment.getPatient().getId());
        response.setPatientUsername(appointment.getPatient().getUsername());
        response.setMedecinId(appointment.getMedecin().getId());
        response.setMedecinUsername(appointment.getMedecin().getUsername());
        response.setAppointmentDate(appointment.getAppointmentDate());
        response.setReason(appointment.getReason());
        response.setStatus(appointment.getStatus().name());
        response.setCreatedAt(appointment.getCreatedAt());
        return response;
    }
}
