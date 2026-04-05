package com.example.paymentservice.dto;

import com.example.paymentservice.enums.PaymentMethod;
import com.example.paymentservice.enums.PaymentStatus;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record PaymentResponse(
        UUID id,
        UUID parcelId,
        PaymentStatus status,
        PaymentMethod paymentMethod,
        BigDecimal amount,
        String currency,
        Instant createdAt,
        Instant updatedAt
) {
}
