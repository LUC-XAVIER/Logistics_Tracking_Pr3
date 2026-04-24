package com.example.logistics_tracking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ParcelQuoteResponse {
    private BigDecimal estimatedCost;
    private LocalDateTime estimatedDeliveryTime;
    private double distanceKm;
    private String sourceAgencyName;
    private String destAgencyName;
}
