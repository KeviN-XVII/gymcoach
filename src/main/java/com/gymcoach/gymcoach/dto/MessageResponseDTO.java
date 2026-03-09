package com.gymcoach.gymcoach.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record MessageResponseDTO(
        UUID id,
        UUID senderId,
        String senderName,
        UUID receiverId,
        String receiverName,
        String content,
        boolean isRead,
        LocalDateTime createdAt
) {}