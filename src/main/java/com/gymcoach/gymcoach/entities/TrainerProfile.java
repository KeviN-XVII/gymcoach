package com.gymcoach.gymcoach.entities;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Entity
@Table(name = "trainer_profile")
@NoArgsConstructor
public class TrainerProfile {

    @Id
    @GeneratedValue
    private UUID id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(columnDefinition = "TEXT")
    private String bio;

    private String specialization;

    @Column(columnDefinition = "TEXT")
    private String certifications;

    @Column(name = "profile_picture")
    private String profilePicture;

    @Column(name = "price_plan")
    private double pricePlan;

    public TrainerProfile(User user, String bio, String specialization, String certifications, String profilePicture, double pricePlan) {
        this.user = user;
        this.bio = bio;
        this.specialization = specialization;
        this.certifications = certifications;
        this.profilePicture = profilePicture;
        this.pricePlan = pricePlan;
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

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public String getCertifications() {
        return certifications;
    }

    public void setCertifications(String certifications) {
        this.certifications = certifications;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public double getPricePlan() {
        return pricePlan;
    }

    public void setPricePlan(double pricePlan) {
        this.pricePlan = pricePlan;
    }

    @Override
    public String toString() {
        return "TrainerProfile{" +
                "id=" + id +
                ", user=" + user +
                ", bio='" + bio + '\'' +
                ", specialization='" + specialization + '\'' +
                ", certifications='" + certifications + '\'' +
                ", profilePicture='" + profilePicture + '\'' +
                ", pricePlan=" + pricePlan +
                '}';
    }
}
