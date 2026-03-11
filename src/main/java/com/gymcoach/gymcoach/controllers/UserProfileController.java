package com.gymcoach.gymcoach.controllers;

import com.gymcoach.gymcoach.dto.UserProfileDTO;
import com.gymcoach.gymcoach.dto.UserProfileResponseDTO;
import com.gymcoach.gymcoach.entities.User;
import com.gymcoach.gymcoach.entities.UserProfile;
import com.gymcoach.gymcoach.exceptions.ValidationException;
import com.gymcoach.gymcoach.services.UserProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserProfileController {

    private final UserProfileService userProfileService;

    @Autowired
    public UserProfileController(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    // GET PROFILO UTENTE
    @GetMapping("/me/profile")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public UserProfileResponseDTO getMyProfile(@AuthenticationPrincipal User currentUser) {
        return userProfileService.findByUserId(currentUser.getId());
    }

    // PUT AGGIORNA PROFILO
    @PutMapping("/me/profile")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public UserProfileResponseDTO updateMyProfile(@AuthenticationPrincipal User currentUser,
                                       @RequestBody @Validated UserProfileDTO payload,
                                       BindingResult validationResult) {
        if (validationResult.hasErrors()) {
            List<String> errorsList = validationResult.getFieldErrors()
                    .stream()
                    .map(fieldError -> fieldError.getDefaultMessage())
                    .toList();
            throw new ValidationException(errorsList);
        }
        return userProfileService.updateProfile(currentUser.getId(), payload);
    }
}
