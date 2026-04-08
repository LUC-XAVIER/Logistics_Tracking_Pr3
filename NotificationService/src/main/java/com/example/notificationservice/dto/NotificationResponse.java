package com.example.notificationservice.dto;

import com.example.notificationservice.enums.NotificationChannel;
import com.example.notificationservice.enums.NotificationEventType;
import java.time.Instant;
import java.util.UUID;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NotificationResponse {

    private UUID id;
    private UUID userId;
    private UUID parcelId;
    private String title;
    private String message;
    private NotificationEventType eventType;
    private NotificationChannel channel;
    private boolean read;
    private Instant createdAt;
}