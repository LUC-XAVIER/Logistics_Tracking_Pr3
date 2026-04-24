package com.example.deliveryservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AgentStats {
    private long totalTrips;
    private long activeTrips;
    private long completedTrips;
    private double totalDistanceKm;
    private long totalParcelsDelivered;
}
