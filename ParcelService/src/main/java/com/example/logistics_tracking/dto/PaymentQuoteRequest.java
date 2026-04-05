package com.example.logistics_tracking.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record PaymentQuoteRequest(
        UUID parcelId,
        BigDecimal weightKg,
        Integer fragilityLevel,
        Double distanceKm
) {
}
