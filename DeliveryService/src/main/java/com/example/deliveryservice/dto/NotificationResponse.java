package com.example.deliveryservice.dto;

import java.time.Instant;
import java.util.UUID;

public record NotificationResponse(
        UUID id,
        UUID parcelId,
        String channel,
        String eventType,
        String status,
        Instant createdAt
) {
}
