package com.skilldev.assessment.service;

import com.skilldev.assessment.entity.Feedback;
import com.skilldev.assessment.repository.FeedbackRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;

    public FeedbackService(FeedbackRepository feedbackRepository) {
        this.feedbackRepository = feedbackRepository;
    }

    @Transactional
    public Feedback submit(Feedback feedback) {
        if (feedback.getRating() == null || feedback.getRating() < 1 || feedback.getRating() > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }
        if (feedback.getComment() == null || feedback.getComment().isBlank()) {
            throw new IllegalArgumentException("Comment is required");
        }
        if (feedback.getTraineeId() == null) {
            throw new IllegalArgumentException("Trainee ID is required");
        }
        return feedbackRepository.save(feedback);
    }

    @Transactional(readOnly = true)
    public List<Feedback> findByTraineeId(Long traineeId) {
        return feedbackRepository.findByTraineeIdOrderByCreatedAtDesc(traineeId);
    }

    @Transactional(readOnly = true)
    public List<Feedback> findByTrainerId(Long trainerId) {
        return feedbackRepository.findByTrainerIdOrderByCreatedAtDesc(trainerId);
    }
}
