package com.skilldev.trainee.repository;

import com.skilldev.trainee.entity.Trainee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TraineeRepository extends JpaRepository<Trainee, Long> {
    Optional<Trainee> findByEmail(String email);
    boolean existsByEmail(String email);
}
