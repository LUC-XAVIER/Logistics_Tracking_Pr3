package com.example.deliveryservice.dto;

import java.util.UUID;

public record ParcelEventNotificationRequest(
        UUID parcelId,
        String eventType,
        String channel,
        String recipientEmail,
        String recipientPhone,
        String message
) {
}
