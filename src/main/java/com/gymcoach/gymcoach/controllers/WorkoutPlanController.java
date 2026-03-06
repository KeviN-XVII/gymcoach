package com.gymcoach.gymcoach.controllers;

import com.gymcoach.gymcoach.dto.WorkoutPlanDTO;
import com.gymcoach.gymcoach.dto.WorkoutPlanResponseDTO;
import com.gymcoach.gymcoach.entities.User;
import com.gymcoach.gymcoach.exceptions.ValidationException;
import com.gymcoach.gymcoach.services.WorkoutPlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/workout-plans")
public class WorkoutPlanController {

    private final WorkoutPlanService workoutPlanService;

    @Autowired
    public WorkoutPlanController(WorkoutPlanService workoutPlanService) {
        this.workoutPlanService = workoutPlanService;
    }

    // GET TUTTE LE SCHEDE DELL'UTENTE AUTENTICATO
    @GetMapping("/me")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public List<WorkoutPlanResponseDTO> getMyPlans(@AuthenticationPrincipal User currentUser) {
        return workoutPlanService.findByUserId(currentUser.getId());
    }

    // GET SCHEDA PER ID
    @GetMapping("/me/{id}")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public WorkoutPlanResponseDTO getPlanById(@AuthenticationPrincipal User currentUser,
                                              @PathVariable UUID id) {
        return workoutPlanService.findByIdAndUserId(id, currentUser.getId());
    }

    // GET SCHEDE DEL TRAINER AUTENTICATO
    @GetMapping("/trainer/me")
    @PreAuthorize("hasAuthority('ROLE_TRAINER')")
    public List<WorkoutPlanResponseDTO> getMyTrainerPlans(@AuthenticationPrincipal User currentUser) {
        return workoutPlanService.findByTrainerProfileId(currentUser.getId());
    }

    // POST TRAINER CREA SCHEDA PER CLIENTE
    @PostMapping("/user/{userId}")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('ROLE_TRAINER')")
    public WorkoutPlanResponseDTO createPlan(@AuthenticationPrincipal User currentUser,
                                             @PathVariable UUID userId,
                                             @RequestBody @Validated WorkoutPlanDTO payload,
                                             BindingResult validationResult) {
        if (validationResult.hasErrors()) {
            List<String> errorsList = validationResult.getFieldErrors()
                    .stream()
                    .map(fieldError -> fieldError.getDefaultMessage())
                    .toList();
            throw new ValidationException(errorsList);
        }
        return workoutPlanService.createPlan(currentUser, payload, userId);
    }

    // DELETE SCHEDA
    @DeleteMapping("/me/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public void deletePlan(@AuthenticationPrincipal User currentUser,
                           @PathVariable UUID id) {
        workoutPlanService.deleteById(id, currentUser.getId());
    }
}
