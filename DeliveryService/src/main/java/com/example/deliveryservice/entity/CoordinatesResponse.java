package com.example.deliveryservice.entity;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CoordinatesResponse {
    private Double latitude;
    private Double longitude;
}