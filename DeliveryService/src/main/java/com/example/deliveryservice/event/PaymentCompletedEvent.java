package com.example.deliveryservice.event;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentCompletedEvent {

    private String eventId;
    private String eventType;
    private LocalDateTime timestamp;
    private String parcelId;
    private String userId;
    private BigDecimal amount;
    private String transactionId;
}