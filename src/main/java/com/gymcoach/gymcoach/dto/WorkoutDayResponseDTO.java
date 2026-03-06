package com.gymcoach.gymcoach.dto;

import java.util.List;
import java.util.UUID;

public record WorkoutDayResponseDTO(
        UUID id,
        int dayNumber,
        String dayName,
        String notes,
        List<ExerciseResponseDTO>exercises
) {}
