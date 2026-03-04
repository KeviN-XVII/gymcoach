package com.gymcoach.gymcoach.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record WorkoutDayDTO(
        @NotNull(message = "Il numero del giorno è obbligatorio")
        @Positive(message = "Il numero del giorno deve essere positivo")
        int dayNumber,

        @NotBlank(message = "Il nome del giorno è obbligatorio")
        String dayName,

        String notes
) {}
