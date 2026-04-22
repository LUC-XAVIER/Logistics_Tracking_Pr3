package com.example.notificationservice.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class ParcelEventPayload {
    private String eventType;
    private UUID parcelId;
    private UUID userId;
    private String sourceAgencyId;
    private String destAgencyId;
    private Double weight;
    private Integer fragility;
    private BigDecimal estimatedCost;
    private LocalDateTime estimatedDeliveryTime;
    private LocalDateTime timestamp;
}