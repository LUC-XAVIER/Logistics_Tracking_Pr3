package com.example.logistics_tracking.dto.old;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record RouteCacheResponse(
        UUID id,
        UUID sourceAgencyId,
        UUID destinationAgencyId,
        String routeData,
        BigDecimal distanceKm,
        Long estimatedMinutes,
        Instant createdAt
) {
}
