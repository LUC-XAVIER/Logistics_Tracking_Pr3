package com.example.deliveryservice.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record ParcelSummaryResponse(
        UUID id,
        BigDecimal weightKg,
        Integer fragilityLevel,
        String status,
        Double sourceLatitude,
        Double sourceLongitude,
        Double destinationLatitude,
        Double destinationLongitude,
        Instant estimatedDeliveryTime
) {
}
