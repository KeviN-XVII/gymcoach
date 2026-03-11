package com.gymcoach.gymcoach.dto;

import com.gymcoach.gymcoach.entities.Gender;
import com.gymcoach.gymcoach.entities.Goal;
import com.gymcoach.gymcoach.entities.Level;
import java.util.UUID;

public record UserProfileResponseDTO(
        UUID id,
        double weightKg,
        double heightCm,
        int age,
        Gender gender,
        Goal goal,
        Level level,
        int weeklyFrequency
) {}
