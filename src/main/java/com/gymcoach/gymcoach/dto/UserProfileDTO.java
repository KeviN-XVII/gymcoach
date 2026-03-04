package com.gymcoach.gymcoach.dto;

import com.gymcoach.gymcoach.entities.Gender;
import com.gymcoach.gymcoach.entities.Goal;
import com.gymcoach.gymcoach.entities.Level;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record UserProfileDTO(
        @NotNull(message = "Il peso è obbligatorio")
        @Positive(message = "Il peso deve essere positivo")
        double weightKg,

        @NotNull(message = "L'altezza è obbligatoria")
        @Positive(message = "L'altezza deve essere positiva")
        double heightCm,

        @NotNull(message = "L'età è obbligatoria")
        @Positive(message = "L'età deve essere positiva")
        int age,

        @NotNull(message = "Il genere è obbligatorio")
        Gender gender,

        @NotNull(message = "L'obiettivo è obbligatorio")
        Goal goal,

        @NotNull(message = "Il livello è obbligatorio")
        Level level,

        @NotNull(message = "La frequenza settimanale è obbligatoria")
        @Positive(message = "La frequenza deve essere positiva")
        int weeklyFrequency
) {}
