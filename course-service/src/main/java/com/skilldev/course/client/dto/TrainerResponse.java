package com.skilldev.course.client.dto;

public record TrainerResponse(
    Long id,
    String name,
    String specialization,
    Integer experienceYears,
    Boolean available,
    String contact,
    String email
) {}
