package com.gymcoach.gymcoach.entities;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "purchases")
@NoArgsConstructor
public class Purchase {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "trainer_id", nullable = false)
    private TrainerProfile trainerProfile;

    @Column(name = "stripe_session_id")
    private String stripeSessionId;

    @Column(name = "stripe_payment_intent")
    private String stripePaymentIntent;

    @Enumerated(EnumType.STRING)
    private PurchaseStatus status;

    private double amount;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @OneToOne(mappedBy = "purchase")
    private WorkoutPlan workoutPlan;

    public Purchase(User user, TrainerProfile trainerProfile, String stripeSessionId, double amount) {
        this.user = user;
        this.trainerProfile = trainerProfile;
        this.stripeSessionId = stripeSessionId;
        this.amount = amount;
        this.status = PurchaseStatus.PENDING;
        this.createdAt = LocalDateTime.now();
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

    public TrainerProfile getTrainerProfile() {
        return trainerProfile;
    }

    public void setTrainerProfile(TrainerProfile trainerProfile) {
        this.trainerProfile = trainerProfile;
    }

    public String getStripeSessionId() {
        return stripeSessionId;
    }

    public void setStripeSessionId(String stripeSessionId) {
        this.stripeSessionId = stripeSessionId;
    }

    public String getStripePaymentIntent() {
        return stripePaymentIntent;
    }

    public void setStripePaymentIntent(String stripePaymentIntent) {
        this.stripePaymentIntent = stripePaymentIntent;
    }

    public PurchaseStatus getStatus() {
        return status;
    }

    public void setStatus(PurchaseStatus status) {
        this.status = status;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public WorkoutPlan getWorkoutPlan() {
        return workoutPlan;
    }

    public void setWorkoutPlan(WorkoutPlan workoutPlan) {
        this.workoutPlan = workoutPlan;
    }

    @Override
    public String toString() {
        return "Purchase{" +
                "id=" + id +
                ", user=" + user +
                ", trainerProfile=" + trainerProfile +
                ", stripeSessionId='" + stripeSessionId + '\'' +
                ", stripePaymentIntent='" + stripePaymentIntent + '\'' +
                ", status=" + status +
                ", amount=" + amount +
                ", createdAt=" + createdAt +
                ", workoutPlan=" + workoutPlan +
                '}';
    }
}
