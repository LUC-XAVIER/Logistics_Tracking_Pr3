package com.example.deliveryservice.dto;

import com.example.deliveryservice.enums.TripStatus;
import lombok.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TripResponse {

    private UUID id;
    private String driverId;
    private UUID sourceAgencyId;
    private UUID destAgencyId;
    private Double totalDistanceKm;
    private Integer segmentCount;
    private TripStatus status;
    private List<SegmentResponse> segments;
    private Integer parcelsCount;
    private Instant startedAt;
    private Instant createdAt;
}