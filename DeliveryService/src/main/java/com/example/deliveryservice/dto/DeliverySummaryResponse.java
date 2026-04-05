package com.example.deliveryservice.dto;

import java.time.Instant;
import java.util.UUID;

public record DeliverySummaryResponse(
        UUID id,
        UUID parcelId,
        String status,
        Double currentLatitude,
        Double currentLongitude,
        Instant estimatedArrival
) {
}
