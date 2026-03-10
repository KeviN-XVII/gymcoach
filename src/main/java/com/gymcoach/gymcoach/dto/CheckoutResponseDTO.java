package com.gymcoach.gymcoach.dto;

public record CheckoutResponseDTO(
        String checkoutUrl,
        String sessionId
) {}
