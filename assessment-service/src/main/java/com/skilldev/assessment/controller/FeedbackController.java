package com.skilldev.assessment.controller;

import com.skilldev.assessment.entity.Feedback;
import com.skilldev.assessment.service.FeedbackService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/feedback")
public class FeedbackController {

    private final FeedbackService feedbackService;

    public FeedbackController(FeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }

    @PostMapping
    public ResponseEntity<Feedback> submit(@RequestBody Feedback feedback) {
        try {
            Feedback saved = feedbackService.submit(feedback);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/trainee/{id}")
    public List<Feedback> listByTrainee(@PathVariable Long id) {
        return feedbackService.findByTraineeId(id);
    }

    @GetMapping("/trainer/{id}")
    public List<Feedback> listByTrainer(@PathVariable Long id) {
        return feedbackService.findByTrainerId(id);
    }
}
