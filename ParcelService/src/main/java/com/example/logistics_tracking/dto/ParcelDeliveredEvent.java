package com.example.logistics_tracking.dto;

import java.time.Instant;
import java.util.UUID;

public record ParcelDeliveredEvent(
        UUID parcelId,
        UUID deliveryId,
        Instant deliveredAt
) {
}
