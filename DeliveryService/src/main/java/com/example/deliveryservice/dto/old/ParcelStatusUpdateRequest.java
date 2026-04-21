package com.example.deliveryservice.dto.old;

import java.util.UUID;

public record ParcelStatusUpdateRequest(
        String status,
        UUID paymentId,
        UUID deliveryId
) {
}
