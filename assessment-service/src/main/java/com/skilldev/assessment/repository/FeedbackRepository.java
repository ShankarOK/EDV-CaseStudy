package com.skilldev.assessment.repository;

import com.skilldev.assessment.entity.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {

    List<Feedback> findByTraineeIdOrderByCreatedAtDesc(Long traineeId);
    List<Feedback> findByTrainerIdOrderByCreatedAtDesc(Long trainerId);
}
