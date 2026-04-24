package com.example.deliveryservice.dto;

import com.example.deliveryservice.enums.SegmentStatus;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SegmentResponse {

    private UUID id;
    private Integer segmentOrder;
    private Double latitude;
    private Double longitude;
    private Double distanceFromStartKm;
    private SegmentStatus status;
    private Instant reachedAt;
}