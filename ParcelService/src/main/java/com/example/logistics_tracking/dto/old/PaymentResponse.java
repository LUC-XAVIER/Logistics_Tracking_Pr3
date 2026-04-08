package com.example.logistics_tracking.dto.old;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record PaymentResponse(
        UUID id,
        UUID parcelId,
        String status,
        String paymentMethod,
        BigDecimal amount,
        String currency,
        Instant createdAt
) {
}
