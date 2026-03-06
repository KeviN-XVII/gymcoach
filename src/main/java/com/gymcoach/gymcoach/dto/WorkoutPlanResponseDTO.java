package com.gymcoach.gymcoach.dto;

import com.gymcoach.gymcoach.entities.Goal;
import com.gymcoach.gymcoach.entities.Level;

import java.util.List;
import java.util.UUID;

public record WorkoutPlanResponseDTO(
        UUID id,
        String title,
        String description,
        Goal goal,
        Level level,
        int durationWeeks,
        double price,
        boolean aiGenerated,
        List<WorkoutDayResponseDTO> workoutDays
) {}