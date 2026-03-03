package com.gymcoach.gymcoach.entities;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Entity
@Table(name = "user_profile")
@NoArgsConstructor
public class UserProfile {

    @Id
    @GeneratedValue
    private UUID id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "weight_kg")
    private double weightKg;

    @Column(name = "height_cm")
    private double heightCm;

    private int age;

    private String gender;

    private String goal;

    private String level;

    @Column(name = "weekly_frequency")
    private int weeklyFrequency;

    public UserProfile(User user, double weightKg, double heightCm, String gender, int age, String goal, String level, int weeklyFrequency) {
        this.user = user;
        this.weightKg = weightKg;
        this.heightCm = heightCm;
        this.gender = gender;
        this.age = age;
        this.goal = goal;
        this.level = level;
        this.weeklyFrequency = weeklyFrequency;
    }

    public UUID getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public double getWeightKg() {
        return weightKg;
    }

    public void setWeightKg(double weightKg) {
        this.weightKg = weightKg;
    }

    public double getHeightCm() {
        return heightCm;
    }

    public void setHeightCm(double heightCm) {
        this.heightCm = heightCm;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getGoal() {
        return goal;
    }

    public void setGoal(String goal) {
        this.goal = goal;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public int getWeeklyFrequency() {
        return weeklyFrequency;
    }

    public void setWeeklyFrequency(int weeklyFrequency) {
        this.weeklyFrequency = weeklyFrequency;
    }

    @Override
    public String toString() {
        return "UserProfile{" +
                "id=" + id +
                ", user=" + user +
                ", weightKg=" + weightKg +
                ", heightCm=" + heightCm +
                ", age=" + age +
                ", gender='" + gender + '\'' +
                ", goal='" + goal + '\'' +
                ", level='" + level + '\'' +
                ", weeklyFrequency=" + weeklyFrequency +
                '}';
    }
}