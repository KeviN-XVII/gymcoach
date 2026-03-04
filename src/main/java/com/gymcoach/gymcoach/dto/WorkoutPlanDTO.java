package com.gymcoach.gymcoach.dto;

import com.gymcoach.gymcoach.entities.Goal;
import com.gymcoach.gymcoach.entities.Level;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record WorkoutPlanDTO(
        @NotBlank(message = "Il titolo è obbligatorio")
        String title,

        String description,

        @NotNull(message = "L'obiettivo è obbligatorio")
        Goal goal,

        @NotNull(message = "Il livello è obbligatorio")
        Level level,

        @NotNull(message = "La durata è obbligatoria")
        @Positive(message = "La durata deve essere positiva")
        int durationWeeks
) {}
