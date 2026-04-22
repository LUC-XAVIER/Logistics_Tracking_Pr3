package com.example.paymentservice.event;

import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentFailedEvent {

    private String eventId;
    private String eventType;
    private Instant timestamp;
    private UUID paymentId;
    private String parcelId;
    private UUID userId;
    private BigDecimal amount;
    private String failureReason;
}