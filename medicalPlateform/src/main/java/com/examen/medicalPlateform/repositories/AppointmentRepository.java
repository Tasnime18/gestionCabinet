package com.examen.medicalPlateform.repositories;

import com.examen.medicalPlateform.models.Appointment;
import com.examen.medicalPlateform.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByPatientId(Long patientId);
    List<Appointment> findByMedecinIdOrderByAppointmentDateDesc(Long medecinId);
    List<Appointment> findByPatientOrderByAppointmentDateDesc(User patient);
    List<Appointment> findByMedecinOrderByAppointmentDateDesc(User medecin);
}
