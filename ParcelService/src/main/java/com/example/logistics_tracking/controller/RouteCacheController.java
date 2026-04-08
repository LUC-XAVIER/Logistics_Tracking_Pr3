package com.example.logistics_tracking.controller;

import com.example.logistics_tracking.dto.old.RouteCacheResponse;
import com.example.logistics_tracking.entity.RouteCache;
import com.example.logistics_tracking.repository.RouteCacheRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/routes")
public class RouteCacheController {

    private final RouteCacheRepository routeCacheRepository;

    public RouteCacheController(RouteCacheRepository routeCacheRepository) {
        this.routeCacheRepository = routeCacheRepository;
    }

    @GetMapping
    public List<RouteCacheResponse> list() {
        return routeCacheRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @GetMapping("/{routeId}")
    public ResponseEntity<RouteCacheResponse> get(@PathVariable UUID routeId) {
        return routeCacheRepository.findById(routeId)
                .map(this::toResponse)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    private RouteCacheResponse toResponse(RouteCache routeCache) {
        return new RouteCacheResponse(
                routeCache.getId(),
                routeCache.getSourceAgencyId(),
                routeCache.getDestinationAgencyId(),
                routeCache.getRouteData(),
                routeCache.getDistanceKm(),
                routeCache.getEstimatedMinutes(),
                routeCache.getCreatedAt()
        );
    }
}
