package com.example.notificationservice.dto;

import java.util.UUID;
import lombok.Data;

@Data
public class ParcelEventPayload {
    private UUID parcelId;
    private UUID userId;
    private String recipientEmail;
    private String recipientPhone;
    private String eventType;       // matches NotificationEventType name
}