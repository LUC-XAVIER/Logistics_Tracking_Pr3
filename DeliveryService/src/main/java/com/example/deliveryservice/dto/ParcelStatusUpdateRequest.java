package com.example.deliveryservice.dto;

import java.util.UUID;

public record ParcelStatusUpdateRequest(
        String status,
        UUID paymentId,
        UUID deliveryId
) {
}
