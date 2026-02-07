package com.skilldev.certification.client.dto;

public record CertificationValidationRequest(
    Long traineeId,
    Long courseId,
    boolean assessmentPassed,
    Integer passingScore,
    Integer traineeScore
) {}
