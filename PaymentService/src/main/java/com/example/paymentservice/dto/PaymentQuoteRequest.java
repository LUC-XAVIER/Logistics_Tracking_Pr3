package com.example.paymentservice.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;

public record PaymentQuoteRequest(
        String parcelId,
        @NotNull @DecimalMin("0.1") BigDecimal weightKg,
        @NotNull @Min(1) @Max(10) Integer fragilityLevel,
        Double distanceKm
) {
}
