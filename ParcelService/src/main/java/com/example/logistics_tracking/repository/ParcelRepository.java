package com.example.logistics_tracking.repository;

import com.example.logistics_tracking.entity.Parcel;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParcelRepository extends JpaRepository<Parcel, UUID> {
}
