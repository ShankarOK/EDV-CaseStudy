package com.skilldev.trainer.repository;

import com.skilldev.trainer.entity.Trainer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TrainerRepository extends JpaRepository<Trainer, Long> {
    List<Trainer> findByAvailableTrue();
    List<Trainer> findBySpecializationIgnoreCase(String specialization);
}
