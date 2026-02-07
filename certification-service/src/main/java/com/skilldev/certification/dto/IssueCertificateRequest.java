package com.skilldev.certification.dto;

public record IssueCertificateRequest(
    Long traineeId,
    Long courseId,
    String courseName,
    Integer passingScore,
    Integer traineeScore
) {}
