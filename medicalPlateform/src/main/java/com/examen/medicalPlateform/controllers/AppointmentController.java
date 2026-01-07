package com.examen.medicalPlateform.controllers;

import com.examen.medicalPlateform.dto.AppointmentRequest;
import com.examen.medicalPlateform.dto.AppointmentResponse;
import com.examen.medicalPlateform.dto.RescheduleRequest;
import com.examen.medicalPlateform.models.User;
import com.examen.medicalPlateform.repositories.UserRepository;
import com.examen.medicalPlateform.services.AppointmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final UserRepository userRepository;

    private User getCurrentUser(UserDetails userDetails) {
        return userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @PostMapping
    public ResponseEntity<AppointmentResponse> createAppointment(
            @Valid @RequestBody AppointmentRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        User patient = getCurrentUser(userDetails);
        AppointmentResponse response = appointmentService.createAppointment(request, patient);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<AppointmentResponse>> getAllAppointments(
            @AuthenticationPrincipal UserDetails userDetails) {
        User patient = getCurrentUser(userDetails);
        List<AppointmentResponse> appointments = appointmentService.getAllPatientAppointments(patient);
        return ResponseEntity.ok(appointments);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AppointmentResponse> getAppointmentById(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        User patient = getCurrentUser(userDetails);
        AppointmentResponse appointment = appointmentService.getAppointmentById(id, patient);
        return ResponseEntity.ok(appointment);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<AppointmentResponse> cancelAppointment(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        User patient = getCurrentUser(userDetails);
        AppointmentResponse appointment = appointmentService.cancelAppointment(id, patient);
        return ResponseEntity.ok(appointment);
    }

    @PutMapping("/{id}/reschedule")
    public ResponseEntity<AppointmentResponse> rescheduleAppointment(
            @PathVariable Long id,
            @Valid @RequestBody RescheduleRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        User patient = getCurrentUser(userDetails);
        AppointmentResponse appointment = appointmentService.rescheduleAppointment(id, request, patient);
        return ResponseEntity.ok(appointment);
    }

    // ========== MEDECIN ENDPOINTS ==========

    @GetMapping("/medecin")
    public ResponseEntity<List<AppointmentResponse>> getMedecinAppointments(
            @AuthenticationPrincipal UserDetails userDetails) {
        User medecin = getCurrentUser(userDetails);
        List<AppointmentResponse> appointments = appointmentService.getMedecinAppointments(medecin);
        return ResponseEntity.ok(appointments);
    }

    @PutMapping("/{id}/accept")
    public ResponseEntity<AppointmentResponse> acceptAppointment(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        User medecin = getCurrentUser(userDetails);
        AppointmentResponse appointment = appointmentService.acceptAppointment(id, medecin);
        return ResponseEntity.ok(appointment);
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<AppointmentResponse> rejectAppointment(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        User medecin = getCurrentUser(userDetails);
        AppointmentResponse appointment = appointmentService.rejectAppointment(id, medecin);
        return ResponseEntity.ok(appointment);
    }

    @PutMapping("/{id}/complete")
    public ResponseEntity<AppointmentResponse> completeAppointment(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        User medecin = getCurrentUser(userDetails);
        AppointmentResponse appointment = appointmentService.completeAppointment(id, medecin);
        return ResponseEntity.ok(appointment);
    }
}
