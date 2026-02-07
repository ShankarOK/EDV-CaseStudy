package com.skilldev.assessment.client.dto;

public record IssueCertificateRequest(
    Long traineeId,
    Long courseId,
    String courseName,
    Integer passingScore,
    Integer traineeScore
) {}
