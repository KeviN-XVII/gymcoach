package com.gymcoach.gymcoach.repositories;

import com.gymcoach.gymcoach.entities.Purchase;
import com.gymcoach.gymcoach.entities.PurchaseStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PurchaseRepository extends JpaRepository<Purchase, UUID> {
    List<Purchase> findByUserId(UUID userId);
    List<Purchase> findByTrainerProfileId(UUID trainerProfileId);
    Optional<Purchase> findByStripeSessionId(String stripeSessionId);
    List<Purchase> findByUserIdAndStatus(UUID userId, PurchaseStatus status);
}
