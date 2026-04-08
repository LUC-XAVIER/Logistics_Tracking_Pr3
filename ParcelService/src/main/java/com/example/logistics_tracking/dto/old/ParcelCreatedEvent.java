package com.example.logistics_tracking.dto.old;

import java.time.Instant;
import java.util.UUID;

public record ParcelCreatedEvent(
        UUID parcelId,
        UUID paymentId,
        Instant occurredAt
) {
}
