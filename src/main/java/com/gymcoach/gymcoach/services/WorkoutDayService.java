package com.gymcoach.gymcoach.services;

import com.gymcoach.gymcoach.dto.ExerciseResponseDTO;
import com.gymcoach.gymcoach.dto.WorkoutDayDTO;
import com.gymcoach.gymcoach.dto.WorkoutDayResponseDTO;
import com.gymcoach.gymcoach.entities.WorkoutDay;
import com.gymcoach.gymcoach.entities.WorkoutPlan;
import com.gymcoach.gymcoach.exceptions.NotFoundException;
import com.gymcoach.gymcoach.repositories.WorkoutDayRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class WorkoutDayService {

    private final WorkoutDayRepository workoutDayRepository;
    private final WorkoutPlanService workoutPlanService;

    @Autowired
    public WorkoutDayService(WorkoutDayRepository workoutDayRepository,
                             WorkoutPlanService workoutPlanService) {
        this.workoutDayRepository = workoutDayRepository;
        this.workoutPlanService = workoutPlanService;
    }

    //UTILIZZO DTO RESPONSE
    private WorkoutDayResponseDTO toResponseDTO(WorkoutDay day) {
        return new WorkoutDayResponseDTO(
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
        );
    }

    // GET TUTTI I GIORNI DI UNA SCHEDA
    public List<WorkoutDayResponseDTO> findByWorkoutPlanId(UUID workoutPlanId) {
        return this.workoutDayRepository.findByWorkoutPlanIdOrderByDayNumber(workoutPlanId)
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    // GET GIORNO PER ID
    public WorkoutDay findById(UUID id) {
        return this.workoutDayRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Giorno con id " + id + " non trovato!"));
    }

    // POST AGGIUNGI GIORNO ALLA SCHEDA
    public WorkoutDayResponseDTO addDay(UUID workoutPlanId, WorkoutDayDTO payload) {
        WorkoutPlan workoutPlan = workoutPlanService.findById(workoutPlanId);

        WorkoutDay workoutDay = new WorkoutDay(
                workoutPlan,
                payload.dayNumber(),
                payload.dayName(),
                payload.notes()
        );

        WorkoutDay savedDay = this.workoutDayRepository.save(workoutDay);
        log.info("Giorno " + savedDay.getDayName() + " aggiunto alla scheda " + workoutPlan.getTitle());
        return toResponseDTO(savedDay);
    }

    // DELETE GIORNO
    public void deleteById(UUID id) {
        WorkoutDay workoutDay = this.findById(id);
        this.workoutDayRepository.delete(workoutDay);
        log.info("Giorno con id " + id + " eliminato con successo!");
    }
}
