package com.skilldev.course.client;

import com.skilldev.course.client.dto.TrainerResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "trainer-service")
public interface TrainerServiceClient {

    @GetMapping("/trainers/{id}")
    TrainerResponse getById(@PathVariable("id") Long id);
}
