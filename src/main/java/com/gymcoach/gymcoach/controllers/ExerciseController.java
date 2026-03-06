package com.gymcoach.gymcoach.controllers;

import com.gymcoach.gymcoach.dto.ExerciseDTO;
import com.gymcoach.gymcoach.dto.ExerciseResponseDTO;
import com.gymcoach.gymcoach.exceptions.ValidationException;
import com.gymcoach.gymcoach.services.ExerciseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/exercises")
public class ExerciseController {

    private final ExerciseService exerciseService;

    @Autowired
    public ExerciseController(ExerciseService exerciseService) {
        this.exerciseService = exerciseService;
    }

    // GET TUTTI GLI ESERCIZI DI UN GIORNO
    @GetMapping("/day/{workoutDayId}")
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_TRAINER')")
    public List<ExerciseResponseDTO> getExercisesByDay(@PathVariable UUID workoutDayId) {
        return exerciseService.findByWorkoutDayId(workoutDayId);
    }

    // POST AGGIUNGE ESERCIZIO AL GIORNO
    @PostMapping("/day/{workoutDayId}")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('ROLE_TRAINER')")
    public ExerciseResponseDTO addExercise(@PathVariable UUID workoutDayId,
                                           @RequestBody @Validated ExerciseDTO payload,
                                           BindingResult validationResult) {
        if (validationResult.hasErrors()) {
            List<String> errorsList = validationResult.getFieldErrors()
                    .stream()
                    .map(fieldError -> fieldError.getDefaultMessage())
                    .toList();
            throw new ValidationException(errorsList);
        }
        return exerciseService.addExercise(workoutDayId, payload);
    }

    // PUT MODIFICA ESERCIZIO
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_TRAINER')")
    public ExerciseResponseDTO updateExercise(@PathVariable UUID id,
                                              @RequestBody @Validated ExerciseDTO payload,
                                              BindingResult validationResult) {
        if (validationResult.hasErrors()) {
            List<String> errorsList = validationResult.getFieldErrors()
                    .stream()
                    .map(fieldError -> fieldError.getDefaultMessage())
                    .toList();
            throw new ValidationException(errorsList);
        }
        return exerciseService.updateExercise(id, payload);
    }

    // DELETE ESERCIZIO
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('ROLE_TRAINER')")
    public void deleteExercise(@PathVariable UUID id) {
        exerciseService.deleteById(id);
    }
}
