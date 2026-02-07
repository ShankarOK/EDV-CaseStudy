package com.skilldev.trainer.service;

import com.skilldev.trainer.entity.Trainer;
import com.skilldev.trainer.repository.TrainerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class TrainerService {

    private final TrainerRepository trainerRepository;

    public TrainerService(TrainerRepository trainerRepository) {
        this.trainerRepository = trainerRepository;
    }

    @Transactional(readOnly = true)
    public List<Trainer> findAll() {
        return trainerRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Trainer> findById(Long id) {
        return trainerRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Trainer> findAvailable() {
        return trainerRepository.findByAvailableTrue();
    }

    @Transactional
    public Trainer save(Trainer trainer) {
        return trainerRepository.save(trainer);
    }

    @Transactional
    public Optional<Trainer> update(Long id, Trainer updates) {
        return trainerRepository.findById(id)
                .map(existing -> {
                    if (updates.getName() != null) existing.setName(updates.getName());
                    if (updates.getSpecialization() != null) existing.setSpecialization(updates.getSpecialization());
                    if (updates.getExperienceYears() != null) existing.setExperienceYears(updates.getExperienceYears());
                    if (updates.getAvailable() != null) existing.setAvailable(updates.getAvailable());
                    if (updates.getContact() != null) existing.setContact(updates.getContact());
                    if (updates.getEmail() != null) existing.setEmail(updates.getEmail());
                    return trainerRepository.save(existing);
                });
    }

    @Transactional
    public boolean deleteById(Long id) {
        if (!trainerRepository.existsById(id)) return false;
        trainerRepository.deleteById(id);
        return true;
    }
}
