package com.skilldev.validation.service;

import com.skilldev.validation.dto.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class ValidationRulesService {

    public ValidationResult validateCourse(CourseValidationRequest req) {
        List<String> errors = new ArrayList<>();
        if (req.durationHours() == null || req.durationHours() <= 0) {
            errors.add("Course duration must be positive");
        }
        if (req.startDate() != null && req.endDate() != null && !req.endDate().isAfter(req.startDate())) {
            errors.add("End date must be after start date");
        }
        if (req.startDate() != null && req.startDate().isBefore(LocalDate.now())) {
            errors.add("Start date cannot be in the past");
        }
        if (req.trainerId() != null && req.trainerId() <= 0) {
            errors.add("Invalid trainer assignment");
        }
        if (req.category() != null && req.category().isBlank()) {
            errors.add("Course category cannot be blank");
        }
        return errors.isEmpty() ? ValidationResult.ok() : ValidationResult.fail(errors);
    }

    public ValidationResult validateTrainee(TraineeValidationRequest req) {
        List<String> errors = new ArrayList<>();
        if (req.email() == null || req.email().isBlank()) {
            errors.add("Email is required");
        } else if (!req.email().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            errors.add("Invalid email format");
        }
        if (req.contact() != null && req.contact().length() < 10) {
            errors.add("Contact number must be at least 10 digits");
        }
        return errors.isEmpty() ? ValidationResult.ok() : ValidationResult.fail(errors);
    }

    public ValidationResult validateTrainer(TrainerValidationRequest req) {
        List<String> errors = new ArrayList<>();
        if (req.specialization() != null && req.courseCategory() != null
                && !req.specialization().equalsIgnoreCase(req.courseCategory())
                && !req.specialization().toLowerCase().contains(req.courseCategory().toLowerCase())) {
            errors.add("Trainer specialization does not match course category");
        }
        if (!req.available()) {
            errors.add("Trainer is not available");
        }
        return errors.isEmpty() ? ValidationResult.ok() : ValidationResult.fail(errors);
    }

    public ValidationResult validateAssessment(AssessmentValidationRequest req) {
        List<String> errors = new ArrayList<>();
        if (req.passingScore() != null && req.maxScore() != null) {
            if (req.passingScore() < 0 || req.passingScore() > req.maxScore()) {
                errors.add("Passing score must be between 0 and max score");
            }
        }
        if (req.traineeScore() != null && req.maxScore() != null
                && (req.traineeScore() < 0 || req.traineeScore() > req.maxScore())) {
            errors.add("Trainee score must be between 0 and max score");
        }
        return errors.isEmpty() ? ValidationResult.ok() : ValidationResult.fail(errors);
    }

    public ValidationResult validateCertification(CertificationValidationRequest req) {
        List<String> errors = new ArrayList<>();
        if (!req.assessmentPassed()) {
            errors.add("Assessment must be passed to issue certificate");
        }
        if (req.traineeScore() != null && req.passingScore() != null
                && req.traineeScore() < req.passingScore()) {
            errors.add("Trainee score below passing score");
        }
        if (req.traineeId() == null || req.courseId() == null) {
            errors.add("Trainee and course are required for certification");
        }
        return errors.isEmpty() ? ValidationResult.ok() : ValidationResult.fail(errors);
    }
}
