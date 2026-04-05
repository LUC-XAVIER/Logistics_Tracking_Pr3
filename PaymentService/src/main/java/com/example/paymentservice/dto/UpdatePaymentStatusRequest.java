package com.example.paymentservice.dto;

import com.example.paymentservice.enums.PaymentStatus;
import jakarta.validation.constraints.NotNull;

public record UpdatePaymentStatusRequest(
        @NotNull PaymentStatus status
) {
}
