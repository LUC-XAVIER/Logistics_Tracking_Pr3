package com.example.deliveryservice.repository;

import com.example.deliveryservice.entity.TripSegment;
import com.example.deliveryservice.enums.SegmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TripSegmentRepository extends JpaRepository<TripSegment, UUID> {

    List<TripSegment> findByTripIdOrderBySegmentOrderAsc(UUID tripId);

    List<TripSegment> findByTripIdAndStatus(UUID tripId, SegmentStatus status);

    Optional<TripSegment> findByTripIdAndSegmentOrder(UUID tripId, Integer segmentOrder);

    long countByTripIdAndStatus(UUID tripId, SegmentStatus status);
}