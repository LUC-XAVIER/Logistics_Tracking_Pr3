package com.example.logistics_tracking.dto.old;

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
