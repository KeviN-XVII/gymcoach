package com.gymcoach.gymcoach.repositories;

import com.gymcoach.gymcoach.entities.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface MessageRepository extends JpaRepository<Message, UUID> {
    List<Message> findByPurchaseIdOrderByCreatedAtAsc(UUID purchaseId);
    List<Message> findByReceiverIdAndIsReadFalse(UUID receiverId);
}
