package com.examen.medicalPlateform.controllers;

import com.examen.medicalPlateform.dto.PatientFileRequest;
import com.examen.medicalPlateform.dto.PatientFileResponse;
import com.examen.medicalPlateform.models.PatientFile;
import com.examen.medicalPlateform.models.User;
import com.examen.medicalPlateform.repositories.UserRepository;
import com.examen.medicalPlateform.services.PatientFileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/patient-files")
@RequiredArgsConstructor
public class PatientFileController {

    private final PatientFileService patientFileService;
    private final UserRepository userRepository;

    // Patient: Consult their own file
    @GetMapping("/my-file")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<PatientFileResponse> getMyFile(Authentication authentication) {
        String username = authentication.getName();
        Optional<PatientFile> patientFile = patientFileService.getPatientFileByUsername(username);

        if (patientFile.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(PatientFileResponse.fromEntity(patientFile.get()));
    }

    // Medecin: Get all patient files
    @GetMapping
    @PreAuthorize("hasRole('MEDECIN')")
    public ResponseEntity<List<PatientFileResponse>> getAllPatientFiles() {
        List<PatientFile> patientFiles = patientFileService.getAllPatientFiles();
        List<PatientFileResponse> responses = patientFiles.stream()
                .map(PatientFileResponse::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    // Medecin: Get patient file by patient ID
    @GetMapping("/patient/{patientId}")
    @PreAuthorize("hasRole('MEDECIN')")
    public ResponseEntity<PatientFileResponse> getPatientFile(@PathVariable Long patientId) {
        Optional<PatientFile> patientFile = patientFileService.getPatientFileByPatientId(patientId);

        if (patientFile.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(PatientFileResponse.fromEntity(patientFile.get()));
    }

    // Medecin: Create a new patient file
    @PostMapping
    @PreAuthorize("hasRole('MEDECIN')")
    public ResponseEntity<PatientFileResponse> createPatientFile(
            @RequestBody PatientFileRequest request,
            Authentication authentication) {

        String medecinUsername = authentication.getName();
        User medecin = userRepository.findByUsername(medecinUsername)
                .orElseThrow(() -> new RuntimeException("Medecin not found"));

        PatientFile patientFile = new PatientFile();
        patientFile.setFirstName(request.getFirstName());
        patientFile.setLastName(request.getLastName());
        patientFile.setDateOfBirth(request.getDateOfBirth());
        patientFile.setGender(request.getGender());
        patientFile.setPhone(request.getPhone());
        patientFile.setEmail(request.getEmail());
        patientFile.setAddress(request.getAddress());
        patientFile.setBloodType(request.getBloodType());
        patientFile.setAllergies(request.getAllergies());
        patientFile.setCurrentMedications(request.getCurrentMedications());
        patientFile.setMedicalHistory(request.getMedicalHistory());
        patientFile.setFamilyMedicalHistory(request.getFamilyMedicalHistory());
        patientFile.setDentalHistory(request.getDentalHistory());
        patientFile.setCurrentDentalIssues(request.getCurrentDentalIssues());
        patientFile.setPreviousTreatments(request.getPreviousTreatments());
        patientFile.setDentalNotes(request.getDentalNotes());
        patientFile.setConsultationNotes(request.getConsultationNotes());

        try {
            PatientFile createdFile = patientFileService.createPatientFile(
                    request.getPatientId(),
                    medecin.getId(),
                    patientFile
            );
            return ResponseEntity.ok(PatientFileResponse.fromEntity(createdFile));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Medecin: Update patient file
    @PutMapping("/patient/{patientId}")
    @PreAuthorize("hasRole('MEDECIN')")
    public ResponseEntity<PatientFileResponse> updatePatientFile(
            @PathVariable Long patientId,
            @RequestBody PatientFileRequest request,
            Authentication authentication) {

        String medecinUsername = authentication.getName();
        User medecin = userRepository.findByUsername(medecinUsername)
                .orElseThrow(() -> new RuntimeException("Medecin not found"));

        PatientFile patientFile = new PatientFile();
        patientFile.setFirstName(request.getFirstName());
        patientFile.setLastName(request.getLastName());
        patientFile.setDateOfBirth(request.getDateOfBirth());
        patientFile.setGender(request.getGender());
        patientFile.setPhone(request.getPhone());
        patientFile.setEmail(request.getEmail());
        patientFile.setAddress(request.getAddress());
        patientFile.setBloodType(request.getBloodType());
        patientFile.setAllergies(request.getAllergies());
        patientFile.setCurrentMedications(request.getCurrentMedications());
        patientFile.setMedicalHistory(request.getMedicalHistory());
        patientFile.setFamilyMedicalHistory(request.getFamilyMedicalHistory());
        patientFile.setDentalHistory(request.getDentalHistory());
        patientFile.setCurrentDentalIssues(request.getCurrentDentalIssues());
        patientFile.setPreviousTreatments(request.getPreviousTreatments());
        patientFile.setDentalNotes(request.getDentalNotes());
        patientFile.setConsultationNotes(request.getConsultationNotes());

        try {
            PatientFile updatedFile = patientFileService.updatePatientFile(
                    patientId,
                    medecin.getId(),
                    patientFile
            );
            return ResponseEntity.ok(PatientFileResponse.fromEntity(updatedFile));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Check if patient file exists
    @GetMapping("/patient/{patientId}/exists")
    @PreAuthorize("hasRole('MEDECIN')")
    public ResponseEntity<Boolean> patientFileExists(@PathVariable Long patientId) {
        return ResponseEntity.ok(patientFileService.patientFileExists(patientId));
    }
}
