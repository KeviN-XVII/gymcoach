package com.gymcoach.gymcoach.dto;

import com.gymcoach.gymcoach.entities.PurchaseStatus;
import java.time.LocalDateTime;
import java.util.UUID;

public record PurchaseResponseDTO(
        UUID id,
        UUID userId,
        String userName,
        UUID trainerProfileId,
        String trainerName,
        double amount,
        PurchaseStatus status,
        LocalDateTime createdAt
) {}