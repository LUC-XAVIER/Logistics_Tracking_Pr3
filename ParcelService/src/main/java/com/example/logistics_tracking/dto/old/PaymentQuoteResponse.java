package com.example.logistics_tracking.dto.old;

import java.math.BigDecimal;
import java.util.UUID;

public record PaymentQuoteResponse(
        UUID parcelId,
        BigDecimal baseAmount,
        BigDecimal fragilityFee,
        BigDecimal distanceFee,
        BigDecimal totalAmount,
        String currency
) {
}
