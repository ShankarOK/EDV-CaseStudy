package com.skilldev.trainee.repository;

import com.skilldev.trainee.entity.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    List<Enrollment> findByTraineeId(Long traineeId);
    List<Enrollment> findByCourseId(Long courseId);
    Optional<Enrollment> findByTraineeIdAndCourseId(Long traineeId, Long courseId);
    boolean existsByTraineeIdAndCourseId(Long traineeId, Long courseId);
}
