package com.example.notificationservice.repository;

import com.example.notificationservice.entity.NotificationMilestone;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationMilestoneRepository extends JpaRepository<NotificationMilestone, UUID> {

    boolean existsByParcelIdAndMilestone(UUID parcelId, Integer milestone);
}