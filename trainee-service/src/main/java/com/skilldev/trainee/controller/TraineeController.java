package com.skilldev.trainee.controller;

import com.skilldev.trainee.entity.Enrollment;
import com.skilldev.trainee.entity.Trainee;
import com.skilldev.trainee.service.TraineeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/trainees")
public class TraineeController {

    private final TraineeService traineeService;

    public TraineeController(TraineeService traineeService) {
        this.traineeService = traineeService;
    }

    @GetMapping
    public List<Trainee> list() {
        return traineeService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Trainee> getById(@PathVariable Long id) {
        return traineeService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Trainee> create(@RequestBody Trainee trainee) {
        try {
            Trainee saved = traineeService.create(trainee);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Trainee> update(@PathVariable Long id, @RequestBody Trainee trainee) {
        try {
            return traineeService.update(id, trainee)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        return traineeService.deleteById(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }

    @GetMapping("/{traineeId}/enrollments")
    public List<Enrollment> getEnrollments(@PathVariable Long traineeId) {
        return traineeService.getEnrollmentsByTrainee(traineeId);
    }

    @PostMapping("/{traineeId}/enroll")
    public ResponseEntity<Enrollment> enroll(@PathVariable Long traineeId, @RequestParam Long courseId) {
        try {
            Enrollment e = traineeService.enroll(traineeId, courseId);
            return ResponseEntity.status(HttpStatus.CREATED).body(e);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PatchMapping("/enrollments/{enrollmentId}/status")
    public ResponseEntity<Enrollment> updateEnrollmentStatus(@PathVariable Long enrollmentId, @RequestParam String status) {
        return traineeService.updateEnrollmentStatus(enrollmentId, status)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
