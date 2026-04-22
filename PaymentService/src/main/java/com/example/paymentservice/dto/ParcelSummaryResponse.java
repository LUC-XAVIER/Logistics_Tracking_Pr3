package com.example.paymentservice.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record ParcelSummaryResponse(
        String id,
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
