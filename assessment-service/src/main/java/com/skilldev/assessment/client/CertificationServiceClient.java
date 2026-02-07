package com.skilldev.assessment.client;

import com.skilldev.assessment.client.dto.IssueCertificateRequest;
import com.skilldev.assessment.client.dto.CertificateResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "certification-service")
public interface CertificationServiceClient {

    @PostMapping("/certificates/issue")
    CertificateResponse issueCertificate(@RequestBody IssueCertificateRequest request);
}
