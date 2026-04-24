package com.example.logistics_tracking.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CoordinatesResponse {
    private Double latitude;
    private Double longitude;
}