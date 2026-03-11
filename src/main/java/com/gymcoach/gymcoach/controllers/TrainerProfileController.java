package com.gymcoach.gymcoach.controllers;
import com.gymcoach.gymcoach.dto.TrainerProfileDTO;
import com.gymcoach.gymcoach.dto.TrainerProfileResponseDTO;
import com.gymcoach.gymcoach.entities.TrainerProfile;
import com.gymcoach.gymcoach.entities.User;
import com.gymcoach.gymcoach.exceptions.ValidationException;
import com.gymcoach.gymcoach.services.TrainerProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/trainers")
public class TrainerProfileController {

    private final TrainerProfileService trainerProfileService;

    @Autowired
    public TrainerProfileController(TrainerProfileService trainerProfileService) {
        this.trainerProfileService = trainerProfileService;
    }

    // GET TRAINER CON PAGINAZIONE
    @GetMapping
    public Page<TrainerProfileResponseDTO> getAllTrainers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String orderBy,
            @RequestParam(defaultValue = "asc") String sortCriteria) {
        return trainerProfileService.findAll(page, size, orderBy, sortCriteria);
    }

    // GET TRAINER PER ID (pubblica)
    @GetMapping("/{id}")
    public TrainerProfileResponseDTO getTrainerById(@PathVariable UUID id) {
        return trainerProfileService.findById(id);
    }

    // GET PROFILO TRAINER
    @GetMapping("/me/profile")
    @PreAuthorize("hasAuthority('ROLE_TRAINER')")
    public TrainerProfileResponseDTO getMyProfile(@AuthenticationPrincipal User currentUser) {
        return trainerProfileService.findByUserIdAsDTO(currentUser.getId());
    }

    // PUT AGGIORNA PROFILO TRAINER
    @PutMapping("/me/profile")
    @PreAuthorize("hasAuthority('ROLE_TRAINER')")
    public TrainerProfileResponseDTO updateMyProfile(@AuthenticationPrincipal User currentUser,
                                          @RequestBody @Validated TrainerProfileDTO payload,
                                          BindingResult validationResult) {
        if (validationResult.hasErrors()) {
            List<String> errorsList = validationResult.getFieldErrors()
                    .stream()
                    .map(fieldError -> fieldError.getDefaultMessage())
                    .toList();
            throw new ValidationException(errorsList);
        }
        return trainerProfileService.updateProfile(currentUser.getId(), payload);
    }
}
