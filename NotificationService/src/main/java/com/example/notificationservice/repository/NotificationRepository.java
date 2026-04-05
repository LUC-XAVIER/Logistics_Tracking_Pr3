package com.example.notificationservice.repository;

import com.example.notificationservice.entity.Notification;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, UUID> {

    List<Notification> findByParcelId(UUID parcelId);
}
