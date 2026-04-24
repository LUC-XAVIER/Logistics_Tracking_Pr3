package com.example.logistics_tracking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AdminStatsResponse {
    private long totalParcels;
    private long activeParcels;
    private long deliveredParcels;
    private BigDecimal totalRevenue;
    private long totalAgencies;
}
