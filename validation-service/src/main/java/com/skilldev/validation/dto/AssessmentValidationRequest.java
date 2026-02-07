package com.skilldev.validation.dto;

public record AssessmentValidationRequest(
    Integer passingScore,
    Integer maxScore,
    Integer traineeScore
) {}
