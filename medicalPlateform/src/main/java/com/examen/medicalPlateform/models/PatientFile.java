package com.examen.medicalPlateform.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "patient_files")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PatientFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "patient_id", nullable = false, unique = true)
    private User patient;

    @ManyToOne
    @JoinColumn(name = "medecin_id")
    private User medecin;

    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private String gender;
    private String phone;
    private String email;
    private String address;

    // Medical information
    private String bloodType;
    private String allergies;
    private String currentMedications;
    private String medicalHistory;
    private String familyMedicalHistory;

    // Dental information
    private String dentalHistory;
    private String currentDentalIssues;
    private String previousTreatments;
    private String dentalNotes;

    // Consultation notes (added by medecin)
    private String consultationNotes;
    private LocalDateTime lastUpdated;
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        lastUpdated = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        lastUpdated = LocalDateTime.now();
    }
}
