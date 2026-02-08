package com.skilldev.certification.controller;

import com.skilldev.certification.dto.IssueCertificateRequest;
import com.skilldev.certification.entity.Certificate;
import com.skilldev.certification.service.CertificateService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/certificates")
public class CertificateController {

    private final CertificateService certificateService;

    public CertificateController(CertificateService certificateService) {
        this.certificateService = certificateService;
    }

    @GetMapping
    public List<Certificate> list() {
        return certificateService.findAll();
    }

    @GetMapping("/trainee/{traineeId}")
    public List<Certificate> listByTrainee(@PathVariable Long traineeId) {
        return certificateService.findByTraineeId(traineeId);
    }

    @GetMapping("/course/{courseId}")
    public List<Certificate> listByCourse(@PathVariable Long courseId) {
        return certificateService.findByCourseId(courseId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Certificate> getById(@PathVariable Long id) {
        return certificateService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<byte[]> downloadPdf(@PathVariable Long id) {
        return certificateService.findById(id)
                .map(cert -> {
                    try {
                        byte[] pdf = certificateService.generatePdf(cert);
                        HttpHeaders headers = new HttpHeaders();
                        headers.setContentType(MediaType.APPLICATION_PDF);
                        headers.setContentDispositionFormData("attachment", "certificate-" + cert.getCertificateCode() + ".pdf");
                        headers.setContentLength(pdf.length);
                        return new ResponseEntity<>(pdf, headers, HttpStatus.OK);
                    } catch (Exception e) {
                        return ResponseEntity.internalServerError().<byte[]>build();
                    }
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/code/{certificateCode}")
    public ResponseEntity<Certificate> getByCode(@PathVariable String certificateCode) {
        return certificateService.findByCode(certificateCode)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/issue")
    public ResponseEntity<Certificate> issue(@RequestBody IssueCertificateRequest request) {
        try {
            Certificate cert = certificateService.issueCertificate(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(cert);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
