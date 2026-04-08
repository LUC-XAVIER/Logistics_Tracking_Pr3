package com.example.notificationservice.dto;

import java.util.UUID;
import lombok.Data;

@Data
public class PaymentEventPayload {
    private UUID paymentId;
    private UUID parcelId;
    private UUID userId;
    private String recipientEmail;
    private String recipientPhone;
    private Double amount;
    private String currency;
    private String eventType;
}