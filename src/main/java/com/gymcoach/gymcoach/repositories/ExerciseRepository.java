package com.gymcoach.gymcoach.repositories;

import com.gymcoach.gymcoach.entities.Exercise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface ExerciseRepository extends JpaRepository<Exercise, UUID> {
    List<Exercise> findByWorkoutDayIdOrderByOrderIndex(UUID workoutDayId);
}
