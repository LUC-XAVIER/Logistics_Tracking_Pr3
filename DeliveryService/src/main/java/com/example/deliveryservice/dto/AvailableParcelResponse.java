package com.example.deliveryservice.dto;

import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AvailableParcelResponse {

    private String parcelId;
    private String userId;
    private Double weight;
    private Integer fragility;
    private BigDecimal estimatedCost;
    private String sourceAgencyName;
    private String destAgencyName;
}