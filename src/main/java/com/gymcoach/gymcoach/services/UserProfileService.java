package com.gymcoach.gymcoach.services;
import com.gymcoach.gymcoach.dto.UserProfileDTO;
import com.gymcoach.gymcoach.dto.UserProfileResponseDTO;
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

    // CONVERSIONE
    private UserProfileResponseDTO toResponseDTO(UserProfile p) {
        return new UserProfileResponseDTO(
                p.getId(), p.getWeightKg(), p.getHeightCm(),
                p.getAge(), p.getGender(), p.getGoal(),
                p.getLevel(), p.getWeeklyFrequency()
        );
    }

    public UserProfileResponseDTO findByUserId(UUID userId) {
        UserProfile profile = this.userProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException("Profilo utente non trovato!"));
        return toResponseDTO(profile);
    }

    public UserProfileResponseDTO updateProfile(UUID userId, UserProfileDTO payload) {
        UserProfile userProfile = this.userProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException("Profilo utente non trovato!"));

        userProfile.setWeightKg(payload.weightKg());
        userProfile.setHeightCm(payload.heightCm());
        userProfile.setAge(payload.age());
        userProfile.setGender(payload.gender());
        userProfile.setGoal(payload.goal());
        userProfile.setLevel(payload.level());
        userProfile.setWeeklyFrequency(payload.weeklyFrequency());

        UserProfile updatedProfile = this.userProfileRepository.save(userProfile);
        log.info("Profilo utente aggiornato con successo");
        return toResponseDTO(updatedProfile);
    }
}
