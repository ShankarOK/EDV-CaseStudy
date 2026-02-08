package com.skilldev.trainee.config;

import com.skilldev.trainee.entity.Trainee;
import com.skilldev.trainee.repository.TraineeRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Seeds one default trainee when the database is empty. The default Security
 * user "trainee" maps to entityId=1, so this ensures id=1 exists for enrollments,
 * assessments, and certificates.
 */
@Component
@Order(1)
public class SeedDataRunner implements ApplicationRunner {

    private final TraineeRepository traineeRepository;

    public SeedDataRunner(TraineeRepository traineeRepository) {
        this.traineeRepository = traineeRepository;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (traineeRepository.count() > 0) {
            return;
        }
        Trainee t = new Trainee();
        t.setName("Demo Trainee");
        t.setEmail("trainee@skilldev.com");
        t.setContact("+1-555-0199");
        t.setQualification("Graduate");
        t.setSkillPreferences("Java, Spring");
        t.setActive(true);
        traineeRepository.save(t);
    }
}
