package com.example.deliveryservice.dto;

import java.time.Instant;
import java.util.UUID;

public record ParcelDeliveredEvent(
        UUID parcelId,
        UUID deliveryId,
        Instant deliveredAt
) {
}
