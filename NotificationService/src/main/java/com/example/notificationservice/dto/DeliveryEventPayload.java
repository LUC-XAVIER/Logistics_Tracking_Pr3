
package com.example.notificationservice.dto;

import lombok.Data;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
public class DeliveryEventPayload {
    private String eventType;
    private String eventId;
    private UUID tripId;
    private UUID segmentId;
    private UUID driverId;
    private Integer segmentOrder;
    private Integer totalSegments;
    private Double latitude;
    private Double longitude;
    private Double distanceTraveledKm;
    private Double distanceRemainingKm;
    private List<String> parcelIds;
    private UUID sourceAgencyId;
    private UUID destAgencyId;
    private Double totalDistanceKm;
    private Instant completedAt;
    private Instant timestamp;
}