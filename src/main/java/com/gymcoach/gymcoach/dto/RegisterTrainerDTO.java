package com.gymcoach.gymcoach.dto;

import jakarta.validation.constraints.*;

public record RegisterTrainerDTO(
        @NotBlank(message = "Il nome è obbligatorio")
        @Size(min = 3, max = 30)
        String firstName,

        @NotBlank(message = "Il cognome è obbligatorio")
        @Size(min = 3, max = 30)
        String lastName,

        @NotBlank(message = "L'email è obbligatoria")
        @Email(message = "Email non valida")
        String email,

        @NotBlank(message = "La password è obbligatoria")
        @Pattern(regexp = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[a-zA-Z]).{4,}$",
                message = "La password deve contenere almeno una maiuscola, una minuscola e un numero")
        String password,

        @NotBlank(message = "La bio è obbligatoria")
        String bio,

        @NotBlank(message = "La specializzazione è obbligatoria")
        String specialization,

        String certifications,

        @NotNull(message = "Il prezzo è obbligatorio")
        @Positive(message = "Il prezzo deve essere positivo")
        double pricePlan
) {}
