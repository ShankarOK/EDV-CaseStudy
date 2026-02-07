package com.skilldev.trainee.client.dto;

public record TraineeValidationRequest(
    String email,
    String contact,
    Long traineeId,
    Long courseId
) {}
