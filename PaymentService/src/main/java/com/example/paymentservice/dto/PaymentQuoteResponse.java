package com.example.paymentservice.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record PaymentQuoteResponse(
        String parcelId,
        BigDecimal baseAmount,
        BigDecimal fragilityFee,
        BigDecimal distanceFee,
        BigDecimal totalAmount,
        String currency
) {
}
