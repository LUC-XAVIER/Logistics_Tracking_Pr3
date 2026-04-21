package com.example.deliveryservice.repository;

import com.example.deliveryservice.entity.DriverTrip;
import com.example.deliveryservice.enums.TripStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DriverTripRepository extends JpaRepository<DriverTrip, UUID> {

    List<DriverTrip> findByDriverId(String driverId);

    List<DriverTrip> findByStatus(TripStatus status);

    List<DriverTrip> findByDriverIdAndStatus(String driverId, TripStatus status);

    List<DriverTrip> findBySourceAgencyIdAndDestAgencyIdAndStatus(
            UUID sourceAgencyId,
            UUID destAgencyId,
            TripStatus status
    );
}