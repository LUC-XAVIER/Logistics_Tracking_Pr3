package com.example.logistics_tracking.dto.old;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record CreateParcelRequest(
        UUID sourceAgencyId,
        UUID destinationAgencyId,
        String sourceAddress,
        String destinationAddress,
        Double sourceLatitude,
        Double sourceLongitude,
        Double destinationLatitude,
        Double destinationLongitude,
        @NotNull @DecimalMin("0.1") BigDecimal weightKg,
        @NotNull @Min(1) @Max(10) Integer fragilityLevel,
        Instant estimatedDeliveryTime,
        String routeSegments
) {
}
