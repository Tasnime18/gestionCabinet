package com.examen.medicalPlateform.dto;

import com.examen.medicalPlateform.models.PatientFile;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PatientFileResponse {
    private Long id;
    private Long patientId;
    private String patientUsername;
    private Long medecinId;
    private String medecinUsername;
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
    private LocalDateTime lastUpdated;
    private LocalDateTime createdAt;

    public static PatientFileResponse fromEntity(PatientFile patientFile) {
        PatientFileResponse response = new PatientFileResponse();
        response.setId(patientFile.getId());
        response.setPatientId(patientFile.getPatient().getId());
        response.setPatientUsername(patientFile.getPatient().getUsername());
        if (patientFile.getMedecin() != null) {
            response.setMedecinId(patientFile.getMedecin().getId());
            response.setMedecinUsername(patientFile.getMedecin().getUsername());
        }
        response.setFirstName(patientFile.getFirstName());
        response.setLastName(patientFile.getLastName());
        response.setDateOfBirth(patientFile.getDateOfBirth());
        response.setGender(patientFile.getGender());
        response.setPhone(patientFile.getPhone());
        response.setEmail(patientFile.getEmail());
        response.setAddress(patientFile.getAddress());
        response.setBloodType(patientFile.getBloodType());
        response.setAllergies(patientFile.getAllergies());
        response.setCurrentMedications(patientFile.getCurrentMedications());
        response.setMedicalHistory(patientFile.getMedicalHistory());
        response.setFamilyMedicalHistory(patientFile.getFamilyMedicalHistory());
        response.setDentalHistory(patientFile.getDentalHistory());
        response.setCurrentDentalIssues(patientFile.getCurrentDentalIssues());
        response.setPreviousTreatments(patientFile.getPreviousTreatments());
        response.setDentalNotes(patientFile.getDentalNotes());
        response.setConsultationNotes(patientFile.getConsultationNotes());
        response.setLastUpdated(patientFile.getLastUpdated());
        response.setCreatedAt(patientFile.getCreatedAt());
        return response;
    }
}
