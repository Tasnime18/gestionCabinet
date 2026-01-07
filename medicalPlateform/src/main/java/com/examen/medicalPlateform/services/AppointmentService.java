package com.examen.medicalPlateform.services;

import com.examen.medicalPlateform.dto.AppointmentRequest;
import com.examen.medicalPlateform.dto.AppointmentResponse;
import com.examen.medicalPlateform.dto.RescheduleRequest;
import com.examen.medicalPlateform.models.Appointment;
import com.examen.medicalPlateform.models.User;
import com.examen.medicalPlateform.repositories.AppointmentRepository;
import com.examen.medicalPlateform.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;

    @Transactional
    public AppointmentResponse createAppointment(AppointmentRequest request, User patient) {
        // Get the default medecin
        User medecin = userRepository.findFirstByRole(User.Role.MEDECIN)
                .orElseThrow(() -> new RuntimeException("No medecin available. Please contact administration."));

        Appointment appointment = new Appointment();
        appointment.setPatient(patient);
        appointment.setMedecin(medecin);
        appointment.setAppointmentDate(request.getAppointmentDate());
        appointment.setReason(request.getReason());
        appointment.setStatus(Appointment.AppointmentStatus.SCHEDULED);

        appointment = appointmentRepository.save(appointment);
        return AppointmentResponse.fromEntity(appointment);
    }

    @Transactional(readOnly = true)
    public List<AppointmentResponse> getAllPatientAppointments(User patient) {
        return appointmentRepository.findByPatientOrderByAppointmentDateDesc(patient)
                .stream()
                .map(AppointmentResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public AppointmentResponse getAppointmentById(Long appointmentId, User patient) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        if (!appointment.getPatient().getId().equals(patient.getId()) &&
            !appointment.getMedecin().getId().equals(patient.getId())) {
            throw new RuntimeException("You are not authorized to view this appointment");
        }

        return AppointmentResponse.fromEntity(appointment);
    }

    @Transactional
    public AppointmentResponse cancelAppointment(Long appointmentId, User patient) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        if (!appointment.getPatient().getId().equals(patient.getId())) {
            throw new RuntimeException("You can only cancel your own appointments");
        }

        if (appointment.getStatus() == Appointment.AppointmentStatus.CANCELLED) {
            throw new RuntimeException("Appointment is already cancelled");
        }

        if (appointment.getStatus() == Appointment.AppointmentStatus.COMPLETED) {
            throw new RuntimeException("Cannot cancel a completed appointment");
        }

        appointment.setStatus(Appointment.AppointmentStatus.CANCELLED);
        appointment = appointmentRepository.save(appointment);
        return AppointmentResponse.fromEntity(appointment);
    }

    @Transactional
    public AppointmentResponse rescheduleAppointment(Long appointmentId, RescheduleRequest request, User patient) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        if (!appointment.getPatient().getId().equals(patient.getId())) {
            throw new RuntimeException("You can only reschedule your own appointments");
        }

        if (appointment.getStatus() == Appointment.AppointmentStatus.CANCELLED) {
            throw new RuntimeException("Cannot reschedule a cancelled appointment");
        }

        if (appointment.getStatus() == Appointment.AppointmentStatus.COMPLETED) {
            throw new RuntimeException("Cannot reschedule a completed appointment");
        }

        appointment.setAppointmentDate(request.getNewAppointmentDate());
        if (request.getReason() != null && !request.getReason().isEmpty()) {
            appointment.setReason(request.getReason());
        }
        appointment.setStatus(Appointment.AppointmentStatus.RESCHEDULED);

        appointment = appointmentRepository.save(appointment);
        return AppointmentResponse.fromEntity(appointment);
    }

    // ========== MEDECIN METHODS ==========

    @Transactional(readOnly = true)
    public List<AppointmentResponse> getMedecinAppointments(User medecin) {
        return appointmentRepository.findByMedecinOrderByAppointmentDateDesc(medecin)
                .stream()
                .map(AppointmentResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public AppointmentResponse acceptAppointment(Long appointmentId, User medecin) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        // Verify the medecin is assigned to this appointment
        if (!appointment.getMedecin().getId().equals(medecin.getId())) {
            throw new RuntimeException("You are not authorized to manage this appointment");
        }

        if (appointment.getStatus() != Appointment.AppointmentStatus.SCHEDULED) {
            throw new RuntimeException("Can only accept appointments in SCHEDULED status");
        }

        appointment.setStatus(Appointment.AppointmentStatus.CONFIRMED);
        appointment = appointmentRepository.save(appointment);
        return AppointmentResponse.fromEntity(appointment);
    }

    @Transactional
    public AppointmentResponse rejectAppointment(Long appointmentId, User medecin) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        // Verify the medecin is assigned to this appointment
        if (!appointment.getMedecin().getId().equals(medecin.getId())) {
            throw new RuntimeException("You are not authorized to manage this appointment");
        }

        if (appointment.getStatus() != Appointment.AppointmentStatus.SCHEDULED) {
            throw new RuntimeException("Can only reject appointments in SCHEDULED status");
        }

        appointment.setStatus(Appointment.AppointmentStatus.REJECTED);
        appointment = appointmentRepository.save(appointment);
        return AppointmentResponse.fromEntity(appointment);
    }

    @Transactional
    public AppointmentResponse completeAppointment(Long appointmentId, User medecin) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        // Verify the medecin is assigned to this appointment
        if (!appointment.getMedecin().getId().equals(medecin.getId())) {
            throw new RuntimeException("You are not authorized to manage this appointment");
        }

        if (appointment.getStatus() != Appointment.AppointmentStatus.CONFIRMED) {
            throw new RuntimeException("Can only complete appointments in CONFIRMED status");
        }

        appointment.setStatus(Appointment.AppointmentStatus.COMPLETED);
        appointment = appointmentRepository.save(appointment);
        return AppointmentResponse.fromEntity(appointment);
    }
}
