package com.skilldev.certification.client;

import com.skilldev.certification.client.dto.CertificationValidationRequest;
import com.skilldev.certification.client.dto.ValidationResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "validation-service")
public interface ValidationServiceClient {

    @PostMapping("/validate/certification")
    ValidationResult validateCertification(@RequestBody CertificationValidationRequest request);
}
