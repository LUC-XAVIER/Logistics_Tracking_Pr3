package com.example.deliveryservice.dto;

import com.example.deliveryservice.enums.DeliveryStatus;
import java.time.Instant;
import java.util.UUID;

public record CreateDeliveryRequest(
        UUID parcelId,
        UUID assignedAgentId,
        DeliveryStatus status,
        Instant estimatedArrival,
        String routeSegments
) {
}
