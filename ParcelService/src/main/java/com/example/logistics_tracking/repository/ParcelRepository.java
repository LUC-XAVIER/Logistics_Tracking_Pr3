package com.example.logistics_tracking.repository;

import com.example.logistics_tracking.entity.Parcel;
import com.example.logistics_tracking.enums.ParcelStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface ParcelRepository extends JpaRepository<Parcel, String> {

    List<Parcel> findByUserId(String userId);

    List<Parcel> findByStatus(ParcelStatus status);

    List<Parcel> findByUserIdAndStatus(String userId, ParcelStatus status);

    List<Parcel> findBySourceAgency_IdAndDestAgency_IdAndStatus(
            UUID sourceAgencyId,
            UUID destAgencyId,
            ParcelStatus status
    );
}