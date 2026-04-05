package com.example.logistics_tracking.repository;

import com.example.logistics_tracking.entity.RouteCache;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RouteCacheRepository extends JpaRepository<RouteCache, UUID> {
}
