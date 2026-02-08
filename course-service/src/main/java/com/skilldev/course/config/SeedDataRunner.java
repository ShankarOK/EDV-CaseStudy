package com.skilldev.course.config;

import com.skilldev.course.entity.Course;
import com.skilldev.course.repository.CourseRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * Seeds one default course when the database is empty. Uses trainerId=1 so
 * Trainer service should be seeded first (or ensure a trainer with id=1 exists).
 * Enables Admin to assign trainers and Trainees to see at least one course.
 */
@Component
@Order(1)
public class SeedDataRunner implements ApplicationRunner {

    private final CourseRepository courseRepository;

    public SeedDataRunner(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (courseRepository.count() > 0) {
            return;
        }
        Course c = new Course();
        c.setTitle("Spring Boot Fundamentals");
        c.setCategory("Backend Development");
        c.setDurationHours(40);
        c.setDescription("Introduction to Spring Boot, REST APIs, and microservices.");
        c.setStartDate(LocalDate.now());
        c.setEndDate(LocalDate.now().plusMonths(3));
        c.setTrainerId(1L);
        c.setActive(true);
        courseRepository.save(c);
    }
}
