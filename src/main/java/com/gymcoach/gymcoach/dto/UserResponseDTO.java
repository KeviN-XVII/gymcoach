package com.gymcoach.gymcoach.dto;

import com.gymcoach.gymcoach.entities.Role;
import java.util.UUID;

public record UserResponseDTO(
        UUID id,
        String firstName,
        String lastName,
        String email,
        Role role,
        String avatar
) {}
