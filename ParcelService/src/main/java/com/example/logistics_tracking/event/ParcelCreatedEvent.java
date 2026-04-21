package com.example.logistics_tracking.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParcelCreatedEvent {

    private String eventId;
    private String eventType;
    private LocalDateTime timestamp;

    private String parcelId;
    private String userId;

    private String sourceAgencyId;
    private String sourceManualAddress;
    private Double sourceLatitude;
    private Double sourceLongitude;

    private String destAgencyId;
    private String destManualAddress;
    private Double destLatitude;
    private Double destLongitude;

    private Double weight;
    private Integer fragility;
    private BigDecimal estimatedCost;
    private LocalDateTime estimatedDeliveryTime;
}