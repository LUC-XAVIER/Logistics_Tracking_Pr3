package com.example.logistics_tracking.dto.old;

import com.example.logistics_tracking.enums.ParcelStatus;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record ParcelResponse(
        UUID id,
        UUID sourceAgencyId,
        UUID destinationAgencyId,
        String sourceAddress,
        String destinationAddress,
        Double sourceLatitude,
        Double sourceLongitude,
        Double destinationLatitude,
        Double destinationLongitude,
        BigDecimal weightKg,
        Integer fragilityLevel,
        ParcelStatus status,
        Instant estimatedDeliveryTime,
        String routeSegments,
        UUID paymentId,
        UUID deliveryId,
        Instant createdAt,
        Instant updatedAt
) {
}
