package com.gymcoach.gymcoach.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record MessageDTO(
        @NotBlank(message = "Il contenuto del messaggio è obbligatorio")
        String content,

        @NotNull(message = "L'id dell'acquisto è obbligatorio")
        UUID purchaseId
) {}