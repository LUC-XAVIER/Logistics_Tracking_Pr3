
package com.example.notificationservice.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
public class PaymentEventPayload {
    private UUID paymentId;
    private String eventType;
    private String eventId;
    private UUID parcelId;
    private UUID userId;
    private BigDecimal amount;
    private String transactionId;
    private Instant timestamp;
}