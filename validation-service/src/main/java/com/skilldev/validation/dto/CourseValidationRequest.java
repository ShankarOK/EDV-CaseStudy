package com.skilldev.validation.dto;

import java.time.LocalDate;

public record CourseValidationRequest(
    Integer durationHours,
    LocalDate startDate,
    LocalDate endDate,
    Long trainerId,
    String category
) {}
