package com.examen.medicalPlateform.services;

import com.examen.medicalPlateform.models.PatientFile;
import com.examen.medicalPlateform.models.User;
import com.examen.medicalPlateform.repositories.PatientFileRepository;
import com.examen.medicalPlateform.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PatientFileService {

    private final PatientFileRepository patientFileRepository;
    private final UserRepository userRepository;

    @Transactional
    public PatientFile createPatientFile(Long patientId, Long medecinId, PatientFile patientFile) {
        User patient = userRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient not found with id: " + patientId));

        if (patientFileRepository.existsByPatientId(patientId)) {
            throw new RuntimeException("Patient file already exists for patient with id: " + patientId);
        }

        patientFile.setPatient(patient);

        if (medecinId != null) {
            User medecin = userRepository.findById(medecinId)
                    .orElseThrow(() -> new RuntimeException("Medecin not found with id: " + medecinId));
            patientFile.setMedecin(medecin);
        }

        return patientFileRepository.save(patientFile);
    }

    @Transactional
    public PatientFile updatePatientFile(Long patientId, Long medecinId, PatientFile updatedFile) {
        PatientFile existingFile = patientFileRepository.findByPatientId(patientId)
                .orElseThrow(() -> new RuntimeException("Patient file not found for patient with id: " + patientId));

        // Update basic info
        if (updatedFile.getFirstName() != null) existingFile.setFirstName(updatedFile.getFirstName());
        if (updatedFile.getLastName() != null) existingFile.setLastName(updatedFile.getLastName());
        if (updatedFile.getDateOfBirth() != null) existingFile.setDateOfBirth(updatedFile.getDateOfBirth());
        if (updatedFile.getGender() != null) existingFile.setGender(updatedFile.getGender());
        if (updatedFile.getPhone() != null) existingFile.setPhone(updatedFile.getPhone());
        if (updatedFile.getEmail() != null) existingFile.setEmail(updatedFile.getEmail());
        if (updatedFile.getAddress() != null) existingFile.setAddress(updatedFile.getAddress());

        // Update medical info
        if (updatedFile.getBloodType() != null) existingFile.setBloodType(updatedFile.getBloodType());
        if (updatedFile.getAllergies() != null) existingFile.setAllergies(updatedFile.getAllergies());
        if (updatedFile.getCurrentMedications() != null) existingFile.setCurrentMedications(updatedFile.getCurrentMedications());
        if (updatedFile.getMedicalHistory() != null) existingFile.setMedicalHistory(updatedFile.getMedicalHistory());
        if (updatedFile.getFamilyMedicalHistory() != null) existingFile.setFamilyMedicalHistory(updatedFile.getFamilyMedicalHistory());

        // Update dental info
        if (updatedFile.getDentalHistory() != null) existingFile.setDentalHistory(updatedFile.getDentalHistory());
        if (updatedFile.getCurrentDentalIssues() != null) existingFile.setCurrentDentalIssues(updatedFile.getCurrentDentalIssues());
        if (updatedFile.getPreviousTreatments() != null) existingFile.setPreviousTreatments(updatedFile.getPreviousTreatments());
        if (updatedFile.getDentalNotes() != null) existingFile.setDentalNotes(updatedFile.getDentalNotes());

        // Update consultation notes (only medecin can update)
        if (updatedFile.getConsultationNotes() != null) existingFile.setConsultationNotes(updatedFile.getConsultationNotes());

        // Update medecin if provided
        if (medecinId != null) {
            User medecin = userRepository.findById(medecinId)
                    .orElseThrow(() -> new RuntimeException("Medecin not found with id: " + medecinId));
            existingFile.setMedecin(medecin);
        }

        return patientFileRepository.save(existingFile);
    }

    public Optional<PatientFile> getPatientFileByPatientId(Long patientId) {
        return patientFileRepository.findByPatientId(patientId);
    }

    public Optional<PatientFile> getPatientFileByUsername(String username) {
        return patientFileRepository.findByPatientUsername(username);
    }

    public List<PatientFile> getAllPatientFiles() {
        return patientFileRepository.findAll();
    }

    public boolean patientFileExists(Long patientId) {
        return patientFileRepository.existsByPatientId(patientId);
    }
}
