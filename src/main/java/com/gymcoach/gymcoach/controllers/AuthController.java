package com.gymcoach.gymcoach.controllers;

import com.gymcoach.gymcoach.dto.*;
import com.gymcoach.gymcoach.entities.User;
import com.gymcoach.gymcoach.exceptions.ValidationException;
import com.gymcoach.gymcoach.services.AuthService;
import com.gymcoach.gymcoach.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    public AuthController(AuthService authService, UserService userService) {
        this.authService = authService;
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseLoginDTO login(@RequestBody LoginDTO body) {
        return new ResponseLoginDTO(this.authService.CheckCredentialsAndGenerateToken(body));
    }

    @PostMapping("/register/user")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponseDTO registerUser(@RequestBody @Validated RegisterUserDTO payload, BindingResult validationResult) {
        if (validationResult.hasErrors()) {
            List<String> errorsList = validationResult.getFieldErrors()
                    .stream()
                    .map(fieldError -> fieldError.getDefaultMessage())
                    .toList();
            throw new ValidationException(errorsList);
        }
        return userService.saveUser(payload);
    }

    @PostMapping("/register/trainer")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponseDTO registerTrainer(@RequestBody @Validated RegisterTrainerDTO payload, BindingResult validationResult) {
        if (validationResult.hasErrors()) {
            List<String> errorsList = validationResult.getFieldErrors()
                    .stream()
                    .map(fieldError -> fieldError.getDefaultMessage())
                    .toList();
            throw new ValidationException(errorsList);
        }
        return userService.saveTrainer(payload);
    }
}
