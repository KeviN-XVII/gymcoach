package com.gymcoach.gymcoach.dto;

import java.util.UUID;

public record ExerciseResponseDTO(
        UUID id,
        String name,
        int sets,
        int reps,
        int restSeconds,
        String notes,
        int orderIndex
) {}
