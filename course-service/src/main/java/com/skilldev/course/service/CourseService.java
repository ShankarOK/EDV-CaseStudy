package com.skilldev.course.service;

import com.skilldev.course.client.ValidationServiceClient;
import com.skilldev.course.client.TrainerServiceClient;
import com.skilldev.course.client.dto.CourseValidationRequest;
import com.skilldev.course.client.dto.ValidationResult;
import com.skilldev.course.entity.Course;
import com.skilldev.course.repository.CourseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CourseService {

    private final CourseRepository courseRepository;
    private final ValidationServiceClient validationServiceClient;
    private final TrainerServiceClient trainerServiceClient;

    public CourseService(CourseRepository courseRepository,
                         ValidationServiceClient validationServiceClient,
                         TrainerServiceClient trainerServiceClient) {
        this.courseRepository = courseRepository;
        this.validationServiceClient = validationServiceClient;
        this.trainerServiceClient = trainerServiceClient;
    }

    @Transactional(readOnly = true)
    public List<Course> findAll() {
        return courseRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Course> findActive() {
        return courseRepository.findByActiveTrue();
    }

    @Transactional(readOnly = true)
    public Optional<Course> findById(Long id) {
        return courseRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Course> findByTrainerId(Long trainerId) {
        return courseRepository.findByTrainerId(trainerId);
    }

    /** Validates course data via Validation Service, then saves. */
    @Transactional
    public Course create(Course course) {
        ValidationResult result = validationServiceClient.validateCourse(
                new CourseValidationRequest(
                        course.getDurationHours(),
                        course.getStartDate(),
                        course.getEndDate(),
                        course.getTrainerId(),
                        course.getCategory()));
        if (!result.valid()) {
            throw new IllegalArgumentException(String.join("; ", result.errors()));
        }
        return courseRepository.save(course);
    }

    @Transactional
    public Optional<Course> update(Long id, Course updates) {
        return courseRepository.findById(id)
                .map(existing -> {
                    if (updates.getTitle() != null) existing.setTitle(updates.getTitle());
                    if (updates.getCategory() != null) existing.setCategory(updates.getCategory());
                    if (updates.getDurationHours() != null) existing.setDurationHours(updates.getDurationHours());
                    if (updates.getDescription() != null) existing.setDescription(updates.getDescription());
                    if (updates.getStartDate() != null) existing.setStartDate(updates.getStartDate());
                    if (updates.getEndDate() != null) existing.setEndDate(updates.getEndDate());
                    if (updates.getTrainerId() != null) existing.setTrainerId(updates.getTrainerId());
                    if (updates.getActive() != null) existing.setActive(updates.getActive());
                    return courseRepository.save(existing);
                });
    }

    @Transactional
    public boolean deleteById(Long id) {
        if (!courseRepository.existsById(id)) return false;
        courseRepository.deleteById(id);
        return true;
    }

    /** Deactivate (soft delete) instead of remove. */
    @Transactional
    public Optional<Course> deactivate(Long id) {
        return courseRepository.findById(id)
                .map(c -> {
                    c.setActive(false);
                    return courseRepository.save(c);
                });
    }
}
