package com.example.deliveryservice.event;

import lombok.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TripStartedEvent {

    private String eventId;
    private String eventType;
    private Instant timestamp;

    private UUID tripId;
    private String driverId;
    private UUID sourceAgencyId;
    private UUID destAgencyId;

    private List<String> parcelIds;
    private Instant startedAt;
}
