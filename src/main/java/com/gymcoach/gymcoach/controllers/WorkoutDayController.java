package com.gymcoach.gymcoach.controllers;

import com.gymcoach.gymcoach.dto.WorkoutDayDTO;
import com.gymcoach.gymcoach.dto.WorkoutDayResponseDTO;
import com.gymcoach.gymcoach.exceptions.ValidationException;
import com.gymcoach.gymcoach.services.WorkoutDayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/workout-days")
public class WorkoutDayController {

    private final WorkoutDayService workoutDayService;

    @Autowired
    public WorkoutDayController(WorkoutDayService workoutDayService) {
        this.workoutDayService = workoutDayService;
    }

    // GET TUTTI I GIORNI DI UNA SCHEDA
    @GetMapping("/plan/{workoutPlanId}")
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_TRAINER')")
    public List<WorkoutDayResponseDTO> getDaysByPlan(@PathVariable UUID workoutPlanId) {
        return workoutDayService.findByWorkoutPlanId(workoutPlanId);
    }

    // POST AGGIUNGE GIORNO ALLA SCHEDA
    @PostMapping("/plan/{workoutPlanId}")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('ROLE_TRAINER')")
    public WorkoutDayResponseDTO addDay(@PathVariable UUID workoutPlanId,
                                        @RequestBody @Validated WorkoutDayDTO payload,
                                        BindingResult validationResult) {
        if (validationResult.hasErrors()) {
            List<String> errorsList = validationResult.getFieldErrors()
                    .stream()
                    .map(fieldError -> fieldError.getDefaultMessage())
                    .toList();
            throw new ValidationException(errorsList);
        }
        return workoutDayService.addDay(workoutPlanId, payload);
    }

    // DELETE GIORNO
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('ROLE_TRAINER')")
    public void deleteDay(@PathVariable UUID id) {
        workoutDayService.deleteById(id);
    }
}