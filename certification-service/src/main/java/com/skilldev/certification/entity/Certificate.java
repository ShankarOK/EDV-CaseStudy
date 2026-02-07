package com.skilldev.certification.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "certificates", uniqueConstraints = @UniqueConstraint(columnNames = {"trainee_id", "course_id"}))
public class Certificate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 64)
    private String certificateCode;

    @Column(name = "trainee_id", nullable = false)
    private Long traineeId;

    @Column(name = "course_id", nullable = false)
    private Long courseId;

    @Column(nullable = false)
    private String courseName;

    private LocalDate issueDate;
    private Integer validityMonths = 24;
    private LocalDateTime issuedAt;

    @PrePersist
    void onCreate() {
        if (issuedAt == null) issuedAt = LocalDateTime.now();
        if (issueDate == null) issueDate = LocalDate.now();
        if (certificateCode == null || certificateCode.isBlank()) {
            certificateCode = "CERT-" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCertificateCode() { return certificateCode; }
    public void setCertificateCode(String certificateCode) { this.certificateCode = certificateCode; }
    public Long getTraineeId() { return traineeId; }
    public void setTraineeId(Long traineeId) { this.traineeId = traineeId; }
    public Long getCourseId() { return courseId; }
    public void setCourseId(Long courseId) { this.courseId = courseId; }
    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }
    public LocalDate getIssueDate() { return issueDate; }
    public void setIssueDate(LocalDate issueDate) { this.issueDate = issueDate; }
    public Integer getValidityMonths() { return validityMonths; }
    public void setValidityMonths(Integer validityMonths) { this.validityMonths = validityMonths; }
    public LocalDateTime getIssuedAt() { return issuedAt; }
    public void setIssuedAt(LocalDateTime issuedAt) { this.issuedAt = issuedAt; }
}
