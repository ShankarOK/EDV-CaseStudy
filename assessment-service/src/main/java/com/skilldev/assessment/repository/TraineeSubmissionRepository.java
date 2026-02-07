package com.skilldev.assessment.repository;

import com.skilldev.assessment.entity.TraineeSubmission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TraineeSubmissionRepository extends JpaRepository<TraineeSubmission, Long> {
    List<TraineeSubmission> findByAssessmentId(Long assessmentId);
    List<TraineeSubmission> findByTraineeId(Long traineeId);
    Optional<TraineeSubmission> findByAssessmentIdAndTraineeId(Long assessmentId, Long traineeId);
    boolean existsByAssessmentIdAndTraineeId(Long assessmentId, Long traineeId);
}
