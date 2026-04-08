package com.example.notificationservice.dto;

import java.util.UUID;
import lombok.Data;

@Data
public class DeliveryEventPayload {
    private UUID parcelId;
    private UUID userId;
    private String recipientEmail;
    private String recipientPhone;
    private Integer progressPercent;  // used for milestone gating
    private String newEta;            // used for DELIVERY_RESCHEDULED
    private String eventType;
}