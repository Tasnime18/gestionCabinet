package com.examen.medicalPlateform.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PatientFileRequest {
    private Long patientId;
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private String gender;
    private String phone;
    private String email;
    private String address;
    private String bloodType;
    private String allergies;
    private String currentMedications;
    private String medicalHistory;
    private String familyMedicalHistory;
    private String dentalHistory;
    private String currentDentalIssues;
    private String previousTreatments;
    private String dentalNotes;
    private String consultationNotes;
}
