package com.skilldev.assessment.client.dto;

public record AssessmentValidationRequest(
    Integer passingScore,
    Integer maxScore,
    Integer traineeScore
) {}
