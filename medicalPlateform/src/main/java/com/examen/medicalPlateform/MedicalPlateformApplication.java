package com.examen.medicalPlateform;

import com.examen.medicalPlateform.models.User;
import com.examen.medicalPlateform.repositories.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class MedicalPlateformApplication {

    public static void main(String[] args) {
        SpringApplication.run(MedicalPlateformApplication.class, args);
    }
@Bean
    public CommandLineRunner createTestUsers(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (!userRepository.existsByUsername("medecin")) {
                User medecin = new User();
                medecin.setUsername("medecin");
                medecin.setPassword(passwordEncoder.encode("medecin123"));
                medecin.setRole(User.Role.MEDECIN);
                userRepository.save(medecin);
                System.out.println("Test medecin created: medecin / medecin123");
            }

            if (!userRepository.existsByUsername("tasnim")) {
                User patient1 = new User();
                patient1.setUsername("tasnim");
                patient1.setPassword(passwordEncoder.encode("tasnim123"));
                patient1.setRole(User.Role.PATIENT);
                userRepository.save(patient1);
                System.out.println("Test patient created: tasnim / tasnim123");
            }

            if (!userRepository.existsByUsername("safa")) {
                User patient2 = new User();
                patient2.setUsername("safa");
                patient2.setPassword(passwordEncoder.encode("safa123"));
                patient2.setRole(User.Role.PATIENT);
                userRepository.save(patient2);
                System.out.println("Test patient created: safa / safa123");
            }
        };
    }
}
