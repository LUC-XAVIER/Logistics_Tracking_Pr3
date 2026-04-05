package com.example.notificationservice.dto;

import com.example.notificationservice.enums.NotificationChannel;
import com.example.notificationservice.enums.NotificationEventType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record SendNotificationRequest(
        UUID parcelId,
        @NotNull NotificationChannel channel,
        @NotNull NotificationEventType eventType,
        @Email String recipientEmail,
        String recipientPhone,
        @NotBlank String message
) {
}
