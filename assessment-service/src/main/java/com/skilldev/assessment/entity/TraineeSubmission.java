package com.skilldev.assessment.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "trainee_submissions", uniqueConstraints = @UniqueConstraint(columnNames = {"assessment_id", "trainee_id"}))
public class TraineeSubmission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "assessment_id", nullable = false)
    private Long assessmentId;

    @Column(name = "trainee_id", nullable = false)
    private Long traineeId;

    private Integer score;
    private Integer maxScore;
    private LocalDateTime submittedAt;
    private LocalDateTime evaluatedAt;
    private Long evaluatedByTrainerId;
    private String status = "SUBMITTED"; // SUBMITTED, EVALUATED

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getAssessmentId() { return assessmentId; }
    public void setAssessmentId(Long assessmentId) { this.assessmentId = assessmentId; }
    public Long getTraineeId() { return traineeId; }
    public void setTraineeId(Long traineeId) { this.traineeId = traineeId; }
    public Integer getScore() { return score; }
    public void setScore(Integer score) { this.score = score; }
    public Integer getMaxScore() { return maxScore; }
    public void setMaxScore(Integer maxScore) { this.maxScore = maxScore; }
    public LocalDateTime getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(LocalDateTime submittedAt) { this.submittedAt = submittedAt; }
    public LocalDateTime getEvaluatedAt() { return evaluatedAt; }
    public void setEvaluatedAt(LocalDateTime evaluatedAt) { this.evaluatedAt = evaluatedAt; }
    public Long getEvaluatedByTrainerId() { return evaluatedByTrainerId; }
    public void setEvaluatedByTrainerId(Long evaluatedByTrainerId) { this.evaluatedByTrainerId = evaluatedByTrainerId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
