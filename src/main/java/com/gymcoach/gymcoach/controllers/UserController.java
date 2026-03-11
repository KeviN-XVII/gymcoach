package com.gymcoach.gymcoach.controllers;

import com.gymcoach.gymcoach.dto.UserDTO;
import com.gymcoach.gymcoach.dto.UserResponseDTO;
import com.gymcoach.gymcoach.entities.User;
import com.gymcoach.gymcoach.exceptions.ValidationException;
import com.gymcoach.gymcoach.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    // GET DATI UTENTE
    @GetMapping("/me")
    public UserResponseDTO getMe(@AuthenticationPrincipal User currentUser) {
        return userService.findByIdAsDTO(currentUser.getId());
    }

    // PUT AGGIORNA DATI UTENTE
    @PutMapping("/me")
    public UserResponseDTO updateMe(@AuthenticationPrincipal User currentUser,
                         @RequestBody @Validated UserDTO payload,
                         BindingResult validationResult) {
        if (validationResult.hasErrors()) {
            List<String> errorsList = validationResult.getFieldErrors()
                    .stream()
                    .map(fieldError -> fieldError.getDefaultMessage())
                    .toList();
            throw new ValidationException(errorsList);
        }
        return userService.updateUser(currentUser.getId(), payload);
    }
    // UPLOAD AVATAR
    @PatchMapping("/me/avatar")
    public UserResponseDTO uploadAvatar(@AuthenticationPrincipal User currentUser,
                             @RequestParam("avatar") MultipartFile file) throws IOException {
        return userService.uploadAvatar(currentUser.getId(), file);
    }




}
