package com.skilldev.validation.dto;

public record TrainerValidationRequest(
    String specialization,
    String courseCategory,
    boolean available
) {}
