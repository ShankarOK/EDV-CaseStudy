package com.skilldev.certification.service;

import com.skilldev.certification.client.ValidationServiceClient;
import com.skilldev.certification.client.dto.CertificationValidationRequest;
import com.skilldev.certification.client.dto.ValidationResult;
import com.skilldev.certification.dto.IssueCertificateRequest;
import com.skilldev.certification.entity.Certificate;
import com.skilldev.certification.repository.CertificateRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
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

    /** Generate PDF for certificate (Phase 5); in-memory, not stored. */
    public byte[] generatePdf(Certificate cert) throws DocumentException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, out);
        document.open();
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
        Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 12);
        document.add(new Paragraph("Certificate of Completion", titleFont));
        document.add(new Paragraph(" "));
        document.add(new Paragraph("This is to certify that", normalFont));
        document.add(new Paragraph("Trainee ID: " + cert.getTraineeId(), normalFont));
        document.add(new Paragraph("has successfully completed", normalFont));
        document.add(new Paragraph(cert.getCourseName(), titleFont));
        document.add(new Paragraph(" "));
        document.add(new Paragraph("Certificate Code: " + cert.getCertificateCode(), normalFont));
        document.add(new Paragraph("Issue Date: " + (cert.getIssueDate() != null ? cert.getIssueDate().format(DateTimeFormatter.ISO_LOCAL_DATE) : "N/A"), normalFont));
        document.add(new Paragraph("Validity: " + (cert.getValidityMonths() != null ? cert.getValidityMonths() + " months" : "N/A"), normalFont));
        document.close();
        return out.toByteArray();
    }
}
