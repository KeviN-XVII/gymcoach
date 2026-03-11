package com.gymcoach.gymcoach.services;
import com.gymcoach.gymcoach.dto.TrainerProfileDTO;
import com.gymcoach.gymcoach.dto.TrainerProfileResponseDTO;
import com.gymcoach.gymcoach.entities.TrainerProfile;
import com.gymcoach.gymcoach.exceptions.NotFoundException;
import com.gymcoach.gymcoach.repositories.TrainerProfileRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
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

    // CONVERSIONE
    private TrainerProfileResponseDTO toResponseDTO(TrainerProfile t) {
        return new TrainerProfileResponseDTO(
                t.getId(),
                t.getUser().getId(),
                t.getUser().getFirstName(),
                t.getUser().getLastName(),
                t.getUser().getAvatar(),
                t.getBio(),
                t.getSpecialization(),
                t.getCertifications(),
                t.getPricePlan()
        );
    }

    // TUTTI I TRAINER CON PAGINAZIONE
    public Page<TrainerProfileResponseDTO> findAll(int page, int size, String orderBy, String sortCriteria) {
        if (size > 100 || size < 0) size = 10;
        if (page < 0) page = 0;
        Pageable pageable = PageRequest.of(page, size,
                sortCriteria.equals("desc") ? Sort.by(orderBy).descending() : Sort.by(orderBy));
        return this.trainerProfileRepository.findAll(pageable).map(this::toResponseDTO);
    }

    // TRAINER PER USER ID — usato internamente
    public TrainerProfile findByUserId(UUID userId) {
        return this.trainerProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException("Profilo trainer non trovato!"));
    }

    // TRAINER PER ID
    public TrainerProfileResponseDTO findById(UUID id) {
        TrainerProfile trainerProfile = this.trainerProfileRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Trainer con id " + id + " non trovato!"));
        return toResponseDTO(trainerProfile);
    }

    public TrainerProfileResponseDTO findByUserIdAsDTO(UUID userId) {
        return toResponseDTO(this.findByUserId(userId));
    }


    // AGGIORNA PROFILO TRAINER
    public TrainerProfileResponseDTO updateProfile(UUID userId, TrainerProfileDTO payload) {
        TrainerProfile trainerProfile = this.findByUserId(userId);
        trainerProfile.setBio(payload.bio());
        trainerProfile.setSpecialization(payload.specialization());
        trainerProfile.setCertifications(payload.certifications());
        trainerProfile.setPricePlan(payload.pricePlan());

        TrainerProfile updatedProfile = this.trainerProfileRepository.save(trainerProfile);
        log.info("Profilo trainer aggiornato con successo");
        return toResponseDTO(updatedProfile);
    }
}
