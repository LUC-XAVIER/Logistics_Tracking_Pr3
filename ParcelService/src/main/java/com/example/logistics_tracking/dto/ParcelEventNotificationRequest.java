package com.example.logistics_tracking.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record ParcelEventNotificationRequest(
        @NotNull UUID parcelId,
        @NotBlank String eventType,
        @NotBlank String channel,
        @Email String recipientEmail,
        String recipientPhone,
        @NotBlank String message
) {
}
