package com.gymcoach.gymcoach.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ExerciseDTO(
        @NotBlank(message = "Il nome dell'esercizio è obbligatorio")
        String name,

        @NotNull(message = "Le serie sono obbligatorie")
        @Positive(message = "Le serie devono essere positive")
        int sets,

        @NotNull(message = "Le ripetizioni sono obbligatorie")
        @Positive(message = "Le ripetizioni devono essere positive")
        int reps,

        @Positive(message = "Il riposo deve essere positivo")
        int restSeconds,

        String notes,

        int orderIndex
) {}
