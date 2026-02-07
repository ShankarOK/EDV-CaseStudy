package com.skilldev.validation.controller;

import com.skilldev.validation.dto.*;
import com.skilldev.validation.service.ValidationRulesService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/validate")
public class ValidationController {

    private final ValidationRulesService validationRulesService;

    public ValidationController(ValidationRulesService validationRulesService) {
        this.validationRulesService = validationRulesService;
    }

    @PostMapping("/course")
    public ResponseEntity<ValidationResult> validateCourse(@RequestBody CourseValidationRequest request) {
        return ResponseEntity.ok(validationRulesService.validateCourse(request));
    }

    @PostMapping("/trainee")
    public ResponseEntity<ValidationResult> validateTrainee(@RequestBody TraineeValidationRequest request) {
        return ResponseEntity.ok(validationRulesService.validateTrainee(request));
    }

    @PostMapping("/trainer")
    public ResponseEntity<ValidationResult> validateTrainer(@RequestBody TrainerValidationRequest request) {
        return ResponseEntity.ok(validationRulesService.validateTrainer(request));
    }

    @PostMapping("/assessment")
    public ResponseEntity<ValidationResult> validateAssessment(@RequestBody AssessmentValidationRequest request) {
        return ResponseEntity.ok(validationRulesService.validateAssessment(request));
    }

    @PostMapping("/certification")
    public ResponseEntity<ValidationResult> validateCertification(@RequestBody CertificationValidationRequest request) {
        return ResponseEntity.ok(validationRulesService.validateCertification(request));
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Validation service is up");
    }
}
