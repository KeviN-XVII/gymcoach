package com.gymcoach.gymcoach.dto;

import java.util.UUID;

public record TrainerProfileResponseDTO(
        UUID id,
        UUID userId,
        String firstName,
        String lastName,
        String avatar,
        String bio,
        String specialization,
        String certifications,
        double pricePlan
) {}
