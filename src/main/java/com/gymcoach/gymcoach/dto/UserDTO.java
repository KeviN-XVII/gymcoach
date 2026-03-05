package com.gymcoach.gymcoach.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UserDTO(
        @Size(min = 3, max = 30, message = "Il nome deve essere tra i 3 e i 30 caratteri")
        String firstName,

        @Size(min = 3, max = 30, message = "Il cognome deve essere tra i 3 e i 30 caratteri")
        String lastName,

        @Email(message = "Email non valida")
        String email,

        @Pattern(regexp = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[a-zA-Z]).{4,}$",
                message = "La password deve contenere almeno una maiuscola, una minuscola e un numero")
        String password
) {}