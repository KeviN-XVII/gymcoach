package com.gymcoach.gymcoach.entities;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Entity
@Table(name = "exercises")
@NoArgsConstructor
public class Exercise {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "workout_day_id", nullable = false)
    private WorkoutDay workoutDay;

    @Column(nullable = false)
    private String name;

    private int sets;
    private int reps;

    @Column(name = "rest_seconds")
    private int restSeconds;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "order_index")
    private int orderIndex;

    public Exercise(WorkoutDay workoutDay, String name, int sets, int reps, int restSeconds, String notes, int orderIndex) {
        this.workoutDay = workoutDay;
        this.name = name;
        this.sets = sets;
        this.reps = reps;
        this.restSeconds = restSeconds;
        this.notes = notes;
        this.orderIndex = orderIndex;
    }

    public UUID getId() {
        return id;
    }

    public WorkoutDay getWorkoutDay() {
        return workoutDay;
    }

    public void setWorkoutDay(WorkoutDay workoutDay) {
        this.workoutDay = workoutDay;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSets() {
        return sets;
    }

    public void setSets(int sets) {
        this.sets = sets;
    }

    public int getReps() {
        return reps;
    }

    public void setReps(int reps) {
        this.reps = reps;
    }

    public int getRestSeconds() {
        return restSeconds;
    }

    public void setRestSeconds(int restSeconds) {
        this.restSeconds = restSeconds;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public int getOrderIndex() {
        return orderIndex;
    }

    public void setOrderIndex(int orderIndex) {
        this.orderIndex = orderIndex;
    }

    @Override
    public String toString() {
        return "Exercise{" +
                "id=" + id +
                ", workoutDay=" + workoutDay +
                ", name='" + name + '\'' +
                ", sets=" + sets +
                ", reps=" + reps +
                ", restSeconds=" + restSeconds +
                ", notes='" + notes + '\'' +
                ", orderIndex=" + orderIndex +
                '}';
    }
}
