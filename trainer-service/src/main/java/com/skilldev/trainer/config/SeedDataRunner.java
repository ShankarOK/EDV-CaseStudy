package com.skilldev.trainer.config;

import com.skilldev.trainer.entity.Trainer;
import com.skilldev.trainer.repository.TrainerRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Seeds default trainers when the database is empty so Admin can register courses
 * and assign trainers without a chicken-and-egg problem.
 */
@Component
@Order(1)
public class SeedDataRunner implements ApplicationRunner {

    private final TrainerRepository trainerRepository;

    public SeedDataRunner(TrainerRepository trainerRepository) {
        this.trainerRepository = trainerRepository;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (trainerRepository.count() > 0) {
            return;
        }
        Trainer t1 = new Trainer();
        t1.setName("John Smith");
        t1.setSpecialization("Java & Spring");
        t1.setExperienceYears(5);
        t1.setAvailable(true);
        t1.setContact("+1-555-0101");
        t1.setEmail("john.smith@skilldev.com");
        trainerRepository.save(t1);

        Trainer t2 = new Trainer();
        t2.setName("Jane Doe");
        t2.setSpecialization("Full Stack Development");
        t2.setExperienceYears(8);
        t2.setAvailable(true);
        t2.setContact("+1-555-0102");
        t2.setEmail("jane.doe@skilldev.com");
        trainerRepository.save(t2);
    }
}
