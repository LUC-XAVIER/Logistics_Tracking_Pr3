package com.example.deliveryservice.dto.old;

import java.time.Instant;
import java.util.UUID;

public record ParcelLocationUpdatedEvent(
        UUID parcelId,
        Double latitude,
        Double longitude,
        Integer progressPercentage,
        Instant occurredAt
) {
}
