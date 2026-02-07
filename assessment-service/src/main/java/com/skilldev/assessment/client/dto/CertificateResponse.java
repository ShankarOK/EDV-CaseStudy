package com.skilldev.assessment.client.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record CertificateResponse(
    Long id,
    String certificateCode,
    Long traineeId,
    Long courseId,
    String courseName,
    LocalDate issueDate,
    Integer validityMonths,
    LocalDateTime issuedAt
) {}
