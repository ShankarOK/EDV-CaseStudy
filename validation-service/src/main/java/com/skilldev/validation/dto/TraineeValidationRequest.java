package com.skilldev.validation.dto;

public record TraineeValidationRequest(
    String email,
    String contact,
    Long traineeId,
    Long courseId
) {}
