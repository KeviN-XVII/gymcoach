package com.gymcoach.gymcoach.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record TrainerProfileDTO(
        @NotBlank(message = "La bio è obbligatoria")
        String bio,

        @NotBlank(message = "La specializzazione è obbligatoria")
        String specialization,

        String certifications,

        @Positive(message = "Il prezzo deve essere positivo")
        double pricePlan
) {}
