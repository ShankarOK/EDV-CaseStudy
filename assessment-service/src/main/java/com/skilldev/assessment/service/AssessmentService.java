package com.skilldev.assessment.service;

import com.skilldev.assessment.client.CertificationServiceClient;
import com.skilldev.assessment.client.ValidationServiceClient;
import com.skilldev.assessment.client.dto.AssessmentValidationRequest;
import com.skilldev.assessment.client.dto.IssueCertificateRequest;
import com.skilldev.assessment.client.dto.ValidationResult;
import com.skilldev.assessment.entity.Assessment;
import com.skilldev.assessment.entity.Question;
import com.skilldev.assessment.entity.TraineeSubmission;
import com.skilldev.assessment.repository.AssessmentRepository;
import com.skilldev.assessment.repository.QuestionRepository;
import com.skilldev.assessment.repository.TraineeSubmissionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AssessmentService {

    private final AssessmentRepository assessmentRepository;
    private final QuestionRepository questionRepository;
    private final TraineeSubmissionRepository submissionRepository;
    private final ValidationServiceClient validationServiceClient;
    private final CertificationServiceClient certificationServiceClient;

    public AssessmentService(AssessmentRepository assessmentRepository,
                             QuestionRepository questionRepository,
                             TraineeSubmissionRepository submissionRepository,
                             ValidationServiceClient validationServiceClient,
                             CertificationServiceClient certificationServiceClient) {
        this.assessmentRepository = assessmentRepository;
        this.questionRepository = questionRepository;
        this.submissionRepository = submissionRepository;
        this.validationServiceClient = validationServiceClient;
        this.certificationServiceClient = certificationServiceClient;
    }

    @Transactional(readOnly = true)
    public List<Assessment> findAll() {
        return assessmentRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Assessment> findById(Long id) {
        return assessmentRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Assessment> findByCourseId(Long courseId) {
        return assessmentRepository.findByCourseId(courseId);
    }

    @Transactional(readOnly = true)
    public List<Question> getQuestions(Long assessmentId) {
        return questionRepository.findByAssessmentIdOrderById(assessmentId);
    }

    @Transactional
    public Assessment createAssessment(Assessment assessment) {
        if (assessment.getPassingScore() != null && assessment.getMaxScore() != null) {
            ValidationResult result = validationServiceClient.validateAssessment(
                    new AssessmentValidationRequest(
                            assessment.getPassingScore(),
                            assessment.getMaxScore(),
                            null));
            if (!result.valid()) {
                throw new IllegalArgumentException(String.join("; ", result.errors()));
            }
        }
        return assessmentRepository.save(assessment);
    }

    @Transactional
    public Question addQuestion(Long assessmentId, Question question) {
        Assessment assessment = assessmentRepository.findById(assessmentId)
                .orElseThrow(() -> new IllegalArgumentException("Assessment not found"));
        question.setAssessment(assessment);
        return questionRepository.save(question);
    }

    @Transactional
    public Optional<Assessment> updateAssessment(Long id, Assessment updates) {
        return assessmentRepository.findById(id)
                .map(existing -> {
                    if (updates.getTitle() != null) existing.setTitle(updates.getTitle());
                    if (updates.getCourseId() != null) existing.setCourseId(updates.getCourseId());
                    if (updates.getPassingScore() != null) existing.setPassingScore(updates.getPassingScore());
                    if (updates.getMaxScore() != null) existing.setMaxScore(updates.getMaxScore());
                    if (updates.getDueDate() != null) existing.setDueDate(updates.getDueDate());
                    if (updates.getStatus() != null) existing.setStatus(updates.getStatus());
                    return assessmentRepository.save(existing);
                });
    }

    @Transactional
    public TraineeSubmission submitAnswers(Long assessmentId, Long traineeId, Map<Long, String> questionIdToSelectedOption) {
        Assessment assessment = assessmentRepository.findById(assessmentId)
                .orElseThrow(() -> new IllegalArgumentException("Assessment not found"));
        if (submissionRepository.existsByAssessmentIdAndTraineeId(assessmentId, traineeId)) {
            throw new IllegalArgumentException("Already submitted for this assessment");
        }
        List<Question> questions = questionRepository.findByAssessmentIdOrderById(assessmentId);
        int score = 0;
        int maxScore = 0;
        for (Question q : questions) {
            maxScore += (q.getMarksPerQuestion() != null ? q.getMarksPerQuestion() : 1);
            String selected = questionIdToSelectedOption != null ? questionIdToSelectedOption.get(q.getId()) : null;
            if (q.getCorrectOption() != null && q.getCorrectOption().equalsIgnoreCase(selected != null ? selected.trim() : "")) {
                score += (q.getMarksPerQuestion() != null ? q.getMarksPerQuestion() : 1);
            }
        }
        TraineeSubmission sub = new TraineeSubmission();
        sub.setAssessmentId(assessmentId);
        sub.setTraineeId(traineeId);
        sub.setScore(score);
        sub.setMaxScore(maxScore);
        sub.setSubmittedAt(LocalDateTime.now());
        sub.setStatus("SUBMITTED");
        sub = submissionRepository.save(sub);
        return sub;
    }

    @Transactional
    public Optional<TraineeSubmission> evaluateSubmission(Long submissionId, Integer score, Long trainerId, String courseName) {
        return submissionRepository.findById(submissionId)
                .map(sub -> {
                    if (score != null) sub.setScore(score);
                    sub.setEvaluatedAt(LocalDateTime.now());
                    sub.setEvaluatedByTrainerId(trainerId);
                    sub.setStatus("EVALUATED");
                    sub = submissionRepository.save(sub);
                    Assessment assessment = assessmentRepository.findById(sub.getAssessmentId()).orElse(null);
                    if (assessment != null && assessment.getPassingScore() != null
                            && sub.getScore() != null && sub.getScore() >= assessment.getPassingScore()) {
                        try {
                            certificationServiceClient.issueCertificate(new IssueCertificateRequest(
                                    sub.getTraineeId(),
                                    assessment.getCourseId(),
                                    courseName != null ? courseName : "Course",
                                    assessment.getPassingScore(),
                                    sub.getScore()));
                        } catch (Exception e) {
                            // log and continue; certificate may already exist
                        }
                    }
                    return sub;
                });
    }

    @Transactional(readOnly = true)
    public List<TraineeSubmission> getSubmissionsByAssessment(Long assessmentId) {
        return submissionRepository.findByAssessmentId(assessmentId);
    }

    @Transactional(readOnly = true)
    public List<TraineeSubmission> getSubmissionsByTrainee(Long traineeId) {
        return submissionRepository.findByTraineeId(traineeId);
    }

    @Transactional(readOnly = true)
    public Optional<TraineeSubmission> getSubmission(Long assessmentId, Long traineeId) {
        return submissionRepository.findByAssessmentIdAndTraineeId(assessmentId, traineeId);
    }
}
