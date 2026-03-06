package com.gymcoach.gymcoach.services;

import com.gymcoach.gymcoach.dto.ExerciseResponseDTO;
import com.gymcoach.gymcoach.dto.WorkoutDayResponseDTO;
import com.gymcoach.gymcoach.dto.WorkoutPlanDTO;
import com.gymcoach.gymcoach.dto.WorkoutPlanResponseDTO;
import com.gymcoach.gymcoach.entities.*;
import com.gymcoach.gymcoach.exceptions.NotFoundException;
import com.gymcoach.gymcoach.exceptions.UnauthorizedException;
import com.gymcoach.gymcoach.repositories.TrainerProfileRepository;
import com.gymcoach.gymcoach.repositories.WorkoutPlanRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class WorkoutPlanService {

    private final WorkoutPlanRepository workoutPlanRepository;
    private final TrainerProfileRepository trainerProfileRepository;
    private final UserService userService;

    @Autowired
    public WorkoutPlanService(WorkoutPlanRepository workoutPlanRepository,
                              TrainerProfileRepository trainerProfileRepository,
                              UserService userService) {
        this.workoutPlanRepository = workoutPlanRepository;
        this.trainerProfileRepository = trainerProfileRepository;
        this.userService = userService;
    }

    //UTILIZZO DTO RESPONSE
    private WorkoutPlanResponseDTO toResponseDTO(WorkoutPlan p) {
        List<WorkoutDayResponseDTO> days = p.getWorkoutDays()
                .stream()
                .map(day -> new WorkoutDayResponseDTO(
                        day.getId(),
                        day.getDayNumber(),
                        day.getDayName(),
                        day.getNotes(),
                        day.getExercises().stream()
                                .map(e -> new ExerciseResponseDTO(
                                        e.getId(), e.getName(), e.getSets(), e.getReps(),
                                        e.getRestSeconds(), e.getNotes(), e.getOrderIndex()
                                ))
                                .toList()
                ))
                .toList();

        return new WorkoutPlanResponseDTO(
                p.getId(), p.getTitle(), p.getDescription(),
                p.getGoal(), p.getLevel(), p.getDurationWeeks(),
                p.getPrice(), p.isAiGenerated(), days
        );
    }

    // GET TUTTE LE SCHEDE DELL'UTENTE
    public List<WorkoutPlanResponseDTO> findByUserId(UUID userId) {
        return this.workoutPlanRepository.findByUserId(userId)
                .stream().map(this::toResponseDTO).toList();
    }

    // GET SCHEDA PER ID
    public WorkoutPlan findById(UUID id) {
        return this.workoutPlanRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Scheda con id " + id + " non trovata!"));
    }

    // GET SCHEDA PER ID E CONTROLLO CHE APPARTENGA ALL'UTENTE
    public WorkoutPlanResponseDTO findByIdAndUserId(UUID id, UUID userId) {
        WorkoutPlan workoutPlan = this.findById(id);
        if (!workoutPlan.getUser().getId().equals(userId))
            throw new UnauthorizedException("Non hai i permessi per accedere a questa scheda!");
        return toResponseDTO(workoutPlan);
    }

    // GET TUTTE LE SCHEDE DEL TRAINER
    public List<WorkoutPlanResponseDTO> findByTrainerProfileId(UUID userId) {
        TrainerProfile trainerProfile = trainerProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException("Profilo trainer non trovato!"));

        return this.workoutPlanRepository.findByTrainerProfileId(trainerProfile.getId())
                .stream().map(this::toResponseDTO).toList();
    }

    // POST TRAINER CREA SCHEDA PER CLIENTE
    public WorkoutPlanResponseDTO createPlan(User trainer, WorkoutPlanDTO payload, UUID targetUserId) {
        TrainerProfile trainerProfile = trainerProfileRepository.findByUserId(trainer.getId())
                .orElseThrow(() -> new NotFoundException("Profilo trainer non trovato!"));

        User targetUser = userService.findById(targetUserId);

        WorkoutPlan workoutPlan = new WorkoutPlan(
                payload.title(),
                payload.description(),
                payload.goal(),
                payload.level(),
                payload.durationWeeks(),
                trainerProfile.getPricePlan(),
                false,
                trainerProfile,
                targetUser
        );

        WorkoutPlan savedPlan = this.workoutPlanRepository.save(workoutPlan);
        log.info("Scheda creata dal trainer " + trainer.getFirstName() + " per l'utente " + targetUser.getFirstName());
        return toResponseDTO(savedPlan);
    }

    // DELETE SCHEDA
    public void deleteById(UUID id, UUID userId) {
        WorkoutPlan workoutPlan = this.findById(id);
        if (!workoutPlan.getUser().getId().equals(userId))
            throw new UnauthorizedException("Non hai i permessi per accedere a questa scheda!");
        this.workoutPlanRepository.delete(workoutPlan);
        log.info("Scheda con id " + id + " eliminata con successo!");
    }
}
