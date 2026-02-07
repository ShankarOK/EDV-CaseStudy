package com.skilldev.assessment.client;

import com.skilldev.assessment.client.dto.AssessmentValidationRequest;
import com.skilldev.assessment.client.dto.ValidationResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "validation-service")
public interface ValidationServiceClient {

    @PostMapping("/validate/assessment")
    ValidationResult validateAssessment(@RequestBody AssessmentValidationRequest request);
}
