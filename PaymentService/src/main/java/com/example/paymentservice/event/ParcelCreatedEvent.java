package com.example.paymentservice.event;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParcelCreatedEvent {

    private String eventId;
    private String eventType;
    private LocalDateTime timestamp;
    private String parcelId;
    private UUID userId;
    private String sourceAgencyId;
    private String destAgencyId;
    private Double weight;
    private Integer fragility;
    private BigDecimal estimatedCost;
    private LocalDateTime estimatedDeliveryTime;
}