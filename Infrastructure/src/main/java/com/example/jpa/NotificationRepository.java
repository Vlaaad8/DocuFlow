package com.example.jpa;

import com.example.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification,Integer> {
    List<Notification> findAllByRecipient_IdAndRead(int recipientId, boolean read);
}
