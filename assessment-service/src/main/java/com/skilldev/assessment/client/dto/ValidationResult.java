package com.skilldev.assessment.client.dto;

import java.util.List;

public record ValidationResult(boolean valid, List<String> errors) {}
