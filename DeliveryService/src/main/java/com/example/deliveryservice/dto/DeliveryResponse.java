package com.example.deliveryservice.dto;

import com.example.deliveryservice.enums.DeliveryStatus;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record DeliveryResponse(
        UUID id,
        UUID parcelId,
        UUID assignedAgentId,
        DeliveryStatus status,
        Double currentLatitude,
        Double currentLongitude,
        Double progressPercentage,
        Instant estimatedArrival,
        String routeSegments,
        List<DeliveryCheckpointResponse> checkpoints,
        Instant createdAt,
        Instant updatedAt
) {
}
