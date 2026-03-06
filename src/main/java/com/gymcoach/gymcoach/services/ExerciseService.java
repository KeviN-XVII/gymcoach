package com.gymcoach.gymcoach.services;

import com.gymcoach.gymcoach.dto.ExerciseDTO;
import com.gymcoach.gymcoach.dto.ExerciseResponseDTO;
import com.gymcoach.gymcoach.entities.Exercise;
import com.gymcoach.gymcoach.entities.WorkoutDay;
import com.gymcoach.gymcoach.exceptions.NotFoundException;
import com.gymcoach.gymcoach.repositories.ExerciseRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class ExerciseService {

    private final ExerciseRepository exerciseRepository;
    private final WorkoutDayService workoutDayService;

    @Autowired
    public ExerciseService(ExerciseRepository exerciseRepository,
                           WorkoutDayService workoutDayService) {
        this.exerciseRepository = exerciseRepository;
        this.workoutDayService = workoutDayService;
    }

    //UTILIZZO DTO RESPONSE
    private ExerciseResponseDTO toResponseDTO(Exercise e) {
        return new ExerciseResponseDTO(
                e.getId(), e.getName(), e.getSets(), e.getReps(),
                e.getRestSeconds(), e.getNotes(), e.getOrderIndex()
        );
    }

    // GET TUTTI GLI ESERCIZI DI UN GIORNO
    public List<ExerciseResponseDTO> findByWorkoutDayId(UUID workoutDayId) {
        return this.exerciseRepository.findByWorkoutDayIdOrderByOrderIndex(workoutDayId)
                .stream().map(this::toResponseDTO).toList();
    }

    // GET ESERCIZIO PER ID
    public Exercise findById(UUID id) {
        return this.exerciseRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Esercizio con id " + id + " non trovato!"));
    }

    // POST AGGIUNGI ESERCIZIO AL GIORNO
    public ExerciseResponseDTO addExercise(UUID workoutDayId, ExerciseDTO payload) {
        WorkoutDay workoutDay = workoutDayService.findById(workoutDayId);

        Exercise exercise = new Exercise(
                workoutDay,
                payload.name(),
                payload.sets(),
                payload.reps(),
                payload.restSeconds(),
                payload.notes(),
                payload.orderIndex()
        );

        Exercise savedExercise = this.exerciseRepository.save(exercise);
        log.info("Esercizio " + savedExercise.getName() + " aggiunto al giorno " + workoutDay.getDayName());
        return toResponseDTO(savedExercise);
    }

    // PUT MODIFICA ESERCIZIO
    public ExerciseResponseDTO updateExercise(UUID id, ExerciseDTO payload) {
        Exercise found = this.findById(id);

        found.setName(payload.name());
        found.setSets(payload.sets());
        found.setReps(payload.reps());
        found.setRestSeconds(payload.restSeconds());
        found.setNotes(payload.notes());
        found.setOrderIndex(payload.orderIndex());

        Exercise updatedExercise = this.exerciseRepository.save(found);
        log.info("Esercizio con id " + updatedExercise.getId() + " modificato con successo!");
        return toResponseDTO(updatedExercise);
    }

    // DELETE ESERCIZIO
    public void deleteById(UUID id) {
        Exercise exercise = this.findById(id);
        this.exerciseRepository.delete(exercise);
        log.info("Esercizio con id " + id + " eliminato con successo!");
    }
}
