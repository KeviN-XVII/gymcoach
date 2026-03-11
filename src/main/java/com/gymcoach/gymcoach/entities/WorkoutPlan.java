package com.gymcoach.gymcoach.entities;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "workout_plans")
@NoArgsConstructor
public class WorkoutPlan {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    private Goal goal;

    @Enumerated(EnumType.STRING)
    private Level level;

    @Column(name = "duration_weeks")
    private int durationWeeks;

    private double price;

    @Column(name = "ai_generated")
    private boolean aiGenerated;

    @ManyToOne
    @JoinColumn(name = "trainer_profile_id")
    private TrainerProfile trainerProfile;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToOne
    @JoinColumn(name = "purchase_id")
    private Purchase purchase;

    @OneToMany(mappedBy = "workoutPlan", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WorkoutDay> workoutDays = new ArrayList<>();



    public WorkoutPlan(String title, String description, Goal goal, Level level, int durationWeeks, double price, boolean aiGenerated, TrainerProfile trainerProfile, User user) {
        this.title = title;
        this.description = description;
        this.goal = goal;
        this.level = level;
        this.durationWeeks = durationWeeks;
        this.price = price;
        this.aiGenerated = aiGenerated;
        this.trainerProfile = trainerProfile;
        this.user = user;
    }

    public UUID getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Goal getGoal() {
        return goal;
    }

    public void setGoal(Goal goal) {
        this.goal = goal;
    }

    public Level getLevel() {
        return level;
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    public int getDurationWeeks() {
        return durationWeeks;
    }

    public void setDurationWeeks(int durationWeeks) {
        this.durationWeeks = durationWeeks;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public boolean isAiGenerated() {
        return aiGenerated;
    }

    public void setAiGenerated(boolean aiGenerated) {
        this.aiGenerated = aiGenerated;
    }

    public TrainerProfile getTrainerProfile() {
        return trainerProfile;
    }

    public void setTrainerProfile(TrainerProfile trainerProfile) {
        this.trainerProfile = trainerProfile;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Purchase getPurchase() {
        return purchase;
    }

    public void setPurchase(Purchase purchase) {
        this.purchase = purchase;
    }

    public List<WorkoutDay> getWorkoutDays() {
        return workoutDays;
    }

    public void setWorkoutDays(List<WorkoutDay> workoutDays) {
        this.workoutDays = workoutDays;
    }


}