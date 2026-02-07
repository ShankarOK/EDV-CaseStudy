package com.skilldev.course.client;

import com.skilldev.course.client.dto.CourseValidationRequest;
import com.skilldev.course.client.dto.ValidationResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "validation-service")
public interface ValidationServiceClient {

    @PostMapping("/validate/course")
    ValidationResult validateCourse(@RequestBody CourseValidationRequest request);
}
