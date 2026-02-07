package com.skilldev.trainee.client.dto;

import java.util.List;

public record ValidationResult(boolean valid, List<String> errors) {}
