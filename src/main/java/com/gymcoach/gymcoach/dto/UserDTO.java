package com.gymcoach.gymcoach.dto;

import com.gymcoach.gymcoach.entities.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UserDTO(
        @NotBlank(message = "Il nome è un campo obbligatorio")
        @Size(min = 3, max = 30, message = "Il nome deve essere tra i 3 e i 30 caratteri")
        String firstName,

        @NotBlank(message = "Il cognome è un campo obbligatorio")
        @Size(min = 3, max = 30, message = "Il cognome deve essere tra i 3 e i 30 caratteri")
        String lastName,

        @NotBlank(message = "L'email è un campo obbligatorio")
        @Email(message = "Email non valida")
        String email,

        @NotBlank(message = "La password è obbligatoria")
        @Pattern(regexp = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[a-zA-Z]).{4,}$",
                message = "La password deve contenere almeno una maiuscola, una minuscola e un numero")
        String password,

        @NotNull(message = "Il ruolo è obbligatorio")
        Role role
) {}
