package com.skilldev.certification.repository;

import com.skilldev.certification.entity.Certificate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CertificateRepository extends JpaRepository<Certificate, Long> {
    List<Certificate> findByTraineeId(Long traineeId);
    List<Certificate> findByCourseId(Long courseId);
    Optional<Certificate> findByCertificateCode(String certificateCode);
    Optional<Certificate> findByTraineeIdAndCourseId(Long traineeId, Long courseId);
    boolean existsByTraineeIdAndCourseId(Long traineeId, Long courseId);
}
