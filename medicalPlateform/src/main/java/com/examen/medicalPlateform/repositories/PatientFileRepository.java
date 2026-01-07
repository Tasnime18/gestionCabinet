package com.examen.medicalPlateform.repositories;

import com.examen.medicalPlateform.models.PatientFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PatientFileRepository extends JpaRepository<PatientFile, Long> {
    Optional<PatientFile> findByPatientId(Long patientId);
    Optional<PatientFile> findByPatientUsername(String username);
    boolean existsByPatientId(Long patientId);
}
