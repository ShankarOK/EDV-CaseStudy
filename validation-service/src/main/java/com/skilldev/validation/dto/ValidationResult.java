package com.skilldev.validation.dto;

import java.util.ArrayList;
import java.util.List;

public record ValidationResult(boolean valid, List<String> errors) {
    public static ValidationResult ok() {
        return new ValidationResult(true, List.of());
    }
    public static ValidationResult fail(String... messages) {
        return new ValidationResult(false, List.of(messages));
    }
    public static ValidationResult fail(List<String> messages) {
        return new ValidationResult(false, new ArrayList<>(messages));
    }
}
