package com.gymcoach.gymcoach.services;
import com.gymcoach.gymcoach.dto.TrainerProfileDTO;
import com.gymcoach.gymcoach.entities.TrainerProfile;
import com.gymcoach.gymcoach.exceptions.NotFoundException;
import com.gymcoach.gymcoach.repositories.TrainerProfileRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
public class TrainerProfileService {

    private final TrainerProfileRepository trainerProfileRepository;

    @Autowired
    public TrainerProfileService(TrainerProfileRepository trainerProfileRepository) {
        this.trainerProfileRepository = trainerProfileRepository;
    }

    //TUTTI I TRAINER
    // TROVA TUTTI I TRAINER CON PAGINAZIONE
    public Page<TrainerProfile> findAll(int page, int size, String orderBy, String sortCriteria) {
        if (size > 100 || size < 0) size = 10;
        if (page < 0) page = 0;
        Pageable pageable = PageRequest.of(page, size,
                sortCriteria.equals("desc") ? Sort.by(orderBy).descending() : Sort.by(orderBy));
        return this.trainerProfileRepository.findAll(pageable);
    }

    //TRAINER PER USER ID
    public TrainerProfile findByUserId(UUID userId) {
        return this.trainerProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException("Profilo trainer non trovato!"));
    }

    //TRAINER PER ID
    public TrainerProfile findById(UUID id) {
        return this.trainerProfileRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Trainer con id " + id + " non trovato!"));
    }

    // AGGIORNA PROFILO TRAINER
    public TrainerProfile updateProfile(UUID userId, TrainerProfileDTO payload) {
        // TROVA PROFILO ESISTENTE
        TrainerProfile trainerProfile = this.findByUserId(userId);

        // AGGIORNA CAMPI
        trainerProfile.setBio(payload.bio());
        trainerProfile.setSpecialization(payload.specialization());
        trainerProfile.setCertifications(payload.certifications());
        trainerProfile.setPricePlan(payload.pricePlan());

        // SALVO E RITORNO
        TrainerProfile updatedProfile = this.trainerProfileRepository.save(trainerProfile);
        log.info("Profilo trainer aggiornato con successo");
        return updatedProfile;
    }
}
