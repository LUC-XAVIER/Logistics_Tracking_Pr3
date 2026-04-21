package com.example.deliveryservice.event;

import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SegmentReachedEvent {

    private String eventId;
    private String eventType;
    private Instant timestamp;

    private UUID tripId;
    private UUID segmentId;
    private String driverId;

    private Integer segmentOrder;
    private Integer totalSegments;

    private Double latitude;
    private Double longitude;
    private Double distanceTraveledKm;
    private Double distanceRemainingKm;

    private java.util.List<String> parcelIds;
}