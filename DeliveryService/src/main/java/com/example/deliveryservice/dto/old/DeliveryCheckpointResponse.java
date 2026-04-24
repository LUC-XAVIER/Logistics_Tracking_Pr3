package com.example.deliveryservice.dto.old;

import com.example.deliveryservice.enums.RoadType;
import java.time.Instant;
import java.util.UUID;

public record DeliveryCheckpointResponse(
        UUID id,
        UUID deliveryId,
        Double latitude,
        Double longitude,
        RoadType roadType,
        Integer sequenceNumber,
        Instant recordedAt
) {
}
