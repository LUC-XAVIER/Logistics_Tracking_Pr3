package com.example.logistics_tracking.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.example.logistics_tracking.enums.ParcelStatus;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParcelResponse {

    private String id;
    private UUID userId;

    private String sourceAgencyId;
    private String sourceAgencyName;
    private String sourceManualAddress;
    private Double sourceLatitude;
    private Double sourceLongitude;

    private String destAgencyId;
    private String destAgencyName;
    private String destManualAddress;
    private Double destLatitude;
    private Double destLongitude;

    private String receiverName;
    private String receiverPhone;

    private Double weight;
    private Integer fragility;
    private ParcelStatus status;
    private BigDecimal estimatedCost;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime estimatedDeliveryTime;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
}