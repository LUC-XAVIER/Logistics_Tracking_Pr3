package com.example.deliveryservice.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AgencyCoordinatesResponse {
    private Double latitude;
    private Double longitude;
}