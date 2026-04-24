package com.example.logistics_tracking.dto;

import lombok.*;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgencyResponse {

    private UUID id;
    private String name;
    private String country;
    private String town;
    private String addressLine;
    private Double latitude;
    private Double longitude;
}