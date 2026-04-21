package com.example.deliveryservice.repository;

import com.example.deliveryservice.entity.TripParcel;
import com.example.deliveryservice.enums.TripParcelStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TripParcelRepository extends JpaRepository<TripParcel, UUID> {

    List<TripParcel> findByTripId(UUID tripId);

    List<TripParcel> findByParcelId(String parcelId);

    Optional<TripParcel> findByTripIdAndParcelId(UUID tripId, String parcelId);

    List<TripParcel> findByTripIdAndStatus(UUID tripId, TripParcelStatus status);

    boolean existsByParcelIdAndStatus(String parcelId, TripParcelStatus status);
}