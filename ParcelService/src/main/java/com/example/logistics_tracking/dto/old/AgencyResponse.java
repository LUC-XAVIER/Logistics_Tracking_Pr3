package com.example.logistics_tracking.dto.old;

import java.time.Instant;
import java.util.UUID;

public record AgencyResponse(
        UUID id,
        String name,
        String country,
        String town,
        String addressLine,
        Double latitude,
        Double longitude,
        Instant createdAt,
        Instant updatedAt
) {
}
