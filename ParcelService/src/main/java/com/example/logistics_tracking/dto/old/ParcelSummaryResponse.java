package com.example.logistics_tracking.dto.old;

import com.example.logistics_tracking.enums.ParcelStatus;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record ParcelSummaryResponse(
        UUID id,
        BigDecimal weightKg,
        Integer fragilityLevel,
        ParcelStatus status,
        Double sourceLatitude,
        Double sourceLongitude,
        Double destinationLatitude,
        Double destinationLongitude,
        Instant estimatedDeliveryTime
) {
}
