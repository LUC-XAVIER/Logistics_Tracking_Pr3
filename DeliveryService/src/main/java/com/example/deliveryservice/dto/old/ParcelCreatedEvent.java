package com.example.deliveryservice.dto.old;

import java.time.Instant;
import java.util.UUID;

public record ParcelCreatedEvent(
        UUID parcelId,
        UUID paymentId,
        Instant occurredAt
) {
}
