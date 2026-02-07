package com.skilldev.certification.service;

import com.skilldev.certification.client.ValidationServiceClient;
import com.skilldev.certification.client.dto.CertificationValidationRequest;
import com.skilldev.certification.client.dto.ValidationResult;
import com.skilldev.certification.dto.IssueCertificateRequest;
import com.skilldev.certification.entity.Certificate;
import com.skilldev.certification.repository.CertificateRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CertificateService {

    private final CertificateRepository certificateRepository;
    private final ValidationServiceClient validationServiceClient;

    public CertificateService(CertificateRepository certificateRepository,
                              ValidationServiceClient validationServiceClient) {
        this.certificateRepository = certificateRepository;
        this.validationServiceClient = validationServiceClient;
    }

    @Transactional(readOnly = true)
    public List<Certificate> findAll() {
        return certificateRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Certificate> findById(Long id) {
        return certificateRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Certificate> findByTraineeId(Long traineeId) {
        return certificateRepository.findByTraineeId(traineeId);
    }

    @Transactional(readOnly = true)
    public List<Certificate> findByCourseId(Long courseId) {
        return certificateRepository.findByCourseId(courseId);
    }

    @Transactional(readOnly = true)
    public Optional<Certificate> findByCode(String certificateCode) {
        return certificateRepository.findByCertificateCode(certificateCode);
    }

    @Transactional
    public Certificate issueCertificate(IssueCertificateRequest request) {
        ValidationResult result = validationServiceClient.validateCertification(
                new CertificationValidationRequest(
                        request.traineeId(),
                        request.courseId(),
                        true,
                        request.passingScore(),
                        request.traineeScore()));
        if (!result.valid()) {
            throw new IllegalArgumentException(String.join("; ", result.errors()));
        }
        if (certificateRepository.existsByTraineeIdAndCourseId(request.traineeId(), request.courseId())) {
            return certificateRepository.findByTraineeIdAndCourseId(request.traineeId(), request.courseId())
                    .orElseThrow();
        }
        Certificate cert = new Certificate();
        cert.setTraineeId(request.traineeId());
        cert.setCourseId(request.courseId());
        cert.setCourseName(request.courseName() != null ? request.courseName() : "Course");
        cert.setValidityMonths(24);
        return certificateRepository.save(cert);
    }
}
