package com.gymcoach.gymcoach.repositories;

import com.gymcoach.gymcoach.entities.WorkoutPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface WorkoutPlanRepository extends JpaRepository<WorkoutPlan, UUID> {
    List<WorkoutPlan> findByUserId(UUID userId);
    List<WorkoutPlan> findByTrainerProfileId(UUID trainerProfileId);
    List<WorkoutPlan> findByAiGenerated(Boolean aiGenerated);
}
