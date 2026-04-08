package com.example.notificationservice.dto.old;

import com.example.notificationservice.enums.NotificationChannel;
import com.example.notificationservice.enums.NotificationEventType;
import com.example.notificationservice.enums.NotificationStatus;
import java.time.Instant;
import java.util.UUID;

public record NotificationResponse(
        UUID id,
        UUID parcelId,
        NotificationChannel channel,
        NotificationEventType eventType,
        NotificationStatus status,
        String recipientEmail,
        String recipientPhone,
        String message,
        Instant createdAt,
        Instant updatedAt
) {
}
