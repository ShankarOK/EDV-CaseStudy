package com.skilldev.assessment.controller;

import com.skilldev.assessment.entity.Assessment;
import com.skilldev.assessment.entity.Question;
import com.skilldev.assessment.entity.TraineeSubmission;
import com.skilldev.assessment.service.AssessmentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/assessments")
public class AssessmentController {

    private final AssessmentService assessmentService;

    public AssessmentController(AssessmentService assessmentService) {
        this.assessmentService = assessmentService;
    }

    @GetMapping
    public List<Assessment> list() {
        return assessmentService.findAll();
    }

    @GetMapping("/course/{courseId}")
    public List<Assessment> listByCourse(@PathVariable Long courseId) {
        return assessmentService.findByCourseId(courseId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Assessment> getById(@PathVariable Long id) {
        return assessmentService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/questions")
    public List<Question> getQuestions(@PathVariable Long id) {
        return assessmentService.getQuestions(id);
    }

    @PostMapping
    public ResponseEntity<Assessment> create(@RequestBody Assessment assessment) {
        try {
            Assessment saved = assessmentService.createAssessment(assessment);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{assessmentId}/questions")
    public ResponseEntity<Question> addQuestion(@PathVariable Long assessmentId, @RequestBody Question question) {
        try {
            Question saved = assessmentService.addQuestion(assessmentId, question);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Assessment> update(@PathVariable Long id, @RequestBody Assessment assessment) {
        return assessmentService.updateAssessment(id, assessment)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{assessmentId}/submit")
    public ResponseEntity<TraineeSubmission> submit(
            @PathVariable Long assessmentId,
            @RequestBody SubmitRequest request) {
        try {
            TraineeSubmission sub = assessmentService.submitAnswers(
                    assessmentId,
                    request.traineeId(),
                    request.answers() != null ? request.answers() : Map.of());
            return ResponseEntity.status(HttpStatus.CREATED).body(sub);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/submissions/{submissionId}/evaluate")
    public ResponseEntity<TraineeSubmission> evaluate(
            @PathVariable Long submissionId,
            @RequestParam(required = false) Integer score,
            @RequestParam Long trainerId,
            @RequestParam(required = false) String courseName) {
        return assessmentService.evaluateSubmission(submissionId, score, trainerId, courseName)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{assessmentId}/submissions")
    public List<TraineeSubmission> listSubmissions(@PathVariable Long assessmentId) {
        return assessmentService.getSubmissionsByAssessment(assessmentId);
    }

    @GetMapping("/trainee/{traineeId}/submissions")
    public List<TraineeSubmission> listSubmissionsByTrainee(@PathVariable Long traineeId) {
        return assessmentService.getSubmissionsByTrainee(traineeId);
    }

    @GetMapping("/{assessmentId}/submissions/trainee/{traineeId}")
    public ResponseEntity<TraineeSubmission> getSubmission(
            @PathVariable Long assessmentId,
            @PathVariable Long traineeId) {
        return assessmentService.getSubmission(assessmentId, traineeId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    public record SubmitRequest(Long traineeId, Map<Long, String> answers) {}
}
