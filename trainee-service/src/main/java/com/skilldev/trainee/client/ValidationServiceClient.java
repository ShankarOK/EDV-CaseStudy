package com.skilldev.trainee.client;

import com.skilldev.trainee.client.dto.TraineeValidationRequest;
import com.skilldev.trainee.client.dto.ValidationResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "validation-service")
public interface ValidationServiceClient {

    @PostMapping("/validate/trainee")
    ValidationResult validateTrainee(@RequestBody TraineeValidationRequest request);
}
