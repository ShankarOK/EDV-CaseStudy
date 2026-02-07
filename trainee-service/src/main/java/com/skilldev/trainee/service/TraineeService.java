package com.skilldev.trainee.service;

import com.skilldev.trainee.client.ValidationServiceClient;
import com.skilldev.trainee.client.dto.TraineeValidationRequest;
import com.skilldev.trainee.client.dto.ValidationResult;
import com.skilldev.trainee.entity.Enrollment;
import com.skilldev.trainee.entity.Trainee;
import com.skilldev.trainee.repository.EnrollmentRepository;
import com.skilldev.trainee.repository.TraineeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class TraineeService {

    private final TraineeRepository traineeRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final ValidationServiceClient validationServiceClient;

    public TraineeService(TraineeRepository traineeRepository,
                          EnrollmentRepository enrollmentRepository,
                          ValidationServiceClient validationServiceClient) {
        this.traineeRepository = traineeRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.validationServiceClient = validationServiceClient;
    }

    @Transactional(readOnly = true)
    public List<Trainee> findAll() {
        return traineeRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Trainee> findById(Long id) {
        return traineeRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Trainee> findByEmail(String email) {
        return traineeRepository.findByEmail(email);
    }

    @Transactional
    public Trainee create(Trainee trainee) {
        ValidationResult result = validationServiceClient.validateTrainee(
                new TraineeValidationRequest(
                        trainee.getEmail(),
                        trainee.getContact(),
                        null,
                        null));
        if (!result.valid()) {
            throw new IllegalArgumentException(String.join("; ", result.errors()));
        }
        if (trainee.getEmail() != null && traineeRepository.existsByEmail(trainee.getEmail())) {
            throw new IllegalArgumentException("Email already registered");
        }
        return traineeRepository.save(trainee);
    }

    @Transactional
    public Optional<Trainee> update(Long id, Trainee updates) {
        return traineeRepository.findById(id)
                .map(existing -> {
                    if (updates.getName() != null) existing.setName(updates.getName());
                    if (updates.getContact() != null) existing.setContact(updates.getContact());
                    if (updates.getQualification() != null) existing.setQualification(updates.getQualification());
                    if (updates.getSkillPreferences() != null) existing.setSkillPreferences(updates.getSkillPreferences());
                    if (updates.getActive() != null) existing.setActive(updates.getActive());
                    if (updates.getEmail() != null) {
                        if (!updates.getEmail().equals(existing.getEmail()) && traineeRepository.existsByEmail(updates.getEmail())) {
                            throw new IllegalArgumentException("Email already registered");
                        }
                        existing.setEmail(updates.getEmail());
                    }
                    return traineeRepository.save(existing);
                });
    }

    @Transactional
    public boolean deleteById(Long id) {
        if (!traineeRepository.existsById(id)) return false;
        traineeRepository.deleteById(id);
        return true;
    }

    // --- Enrollments ---

    @Transactional(readOnly = true)
    public List<Enrollment> getEnrollmentsByTrainee(Long traineeId) {
        return enrollmentRepository.findByTraineeId(traineeId);
    }

    @Transactional
    public Enrollment enroll(Long traineeId, Long courseId) {
        if (!traineeRepository.existsById(traineeId)) {
            throw new IllegalArgumentException("Trainee not found");
        }
        if (enrollmentRepository.existsByTraineeIdAndCourseId(traineeId, courseId)) {
            throw new IllegalArgumentException("Already enrolled in this course");
        }
        Enrollment e = new Enrollment();
        e.setTraineeId(traineeId);
        e.setCourseId(courseId);
        e.setStatus("ENROLLED");
        return enrollmentRepository.save(e);
    }

    @Transactional
    public Optional<Enrollment> updateEnrollmentStatus(Long enrollmentId, String status) {
        return enrollmentRepository.findById(enrollmentId)
                .map(e -> {
                    e.setStatus(status);
                    return enrollmentRepository.save(e);
                });
    }
}
