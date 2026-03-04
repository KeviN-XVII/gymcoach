package com.gymcoach.gymcoach.controllers;

import com.gymcoach.gymcoach.dto.LoginDTO;
import com.gymcoach.gymcoach.dto.ResponseLoginDTO;
import com.gymcoach.gymcoach.dto.UserDTO;
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

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public User register(@RequestBody @Validated UserDTO payload, BindingResult validationResult) {
        if (validationResult.hasErrors()) {
            List<String> errorsList = validationResult.getFieldErrors()
                    .stream()
                    .map(fieldError -> fieldError.getDefaultMessage())
                    .toList();
            throw new ValidationException(errorsList);
        }
        return userService.saveUser(payload);
    }
}
