package com.gymcoach.gymcoach.services;
import com.gymcoach.gymcoach.dto.UserProfileDTO;
import com.gymcoach.gymcoach.entities.UserProfile;
import com.gymcoach.gymcoach.exceptions.NotFoundException;
import com.gymcoach.gymcoach.repositories.UserProfileRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
public class UserProfileService {

    private final UserProfileRepository userProfileRepository;

    @Autowired
    public UserProfileService(UserProfileRepository userProfileRepository) {
        this.userProfileRepository = userProfileRepository;
    }

    public UserProfile findByUserId(UUID userId) {
        return this.userProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException("Profilo utente non trovato!"));
    }

    // AGGIORNA PROFILO
    public UserProfile updateProfile(UUID userId, UserProfileDTO payload) {
        UserProfile userProfile = this.findByUserId(userId);
        // AGGIORNA
        userProfile.setWeightKg(payload.weightKg());
        userProfile.setHeightCm(payload.heightCm());
        userProfile.setAge(payload.age());
        userProfile.setGender(payload.gender());
        userProfile.setGoal(payload.goal());
        userProfile.setLevel(payload.level());
        userProfile.setWeeklyFrequency(payload.weeklyFrequency());
        // SALVO
        UserProfile updatedProfile = this.userProfileRepository.save(userProfile);
        log.info("Profilo utente aggiornato con successo");
        return updatedProfile;
    }
}
