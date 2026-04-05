package com.example.logistics_tracking.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "route_cache")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RouteCache {

    @Id
    private UUID id;

    private UUID sourceAgencyId;

    private UUID destinationAgencyId;

    @Lob
    private String routeData;

    private BigDecimal distanceKm;

    private Long estimatedMinutes;

    private Instant createdAt;

    @PrePersist
    void onCreate() {
        if (id == null) {
            id = UUID.randomUUID();
        }
        createdAt = Instant.now();
    }
}
