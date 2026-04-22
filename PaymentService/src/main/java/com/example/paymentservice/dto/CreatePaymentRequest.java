package com.example.paymentservice.dto;

import com.example.paymentservice.enums.PaymentMethod;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;

public record CreatePaymentRequest(
        @NotNull String parcelId,
        @NotNull PaymentMethod paymentMethod,
        BigDecimal quotedAmount,
        String currency
) {
}
