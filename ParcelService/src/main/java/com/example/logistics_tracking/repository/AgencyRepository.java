package com.example.logistics_tracking.repository;

import com.example.logistics_tracking.entity.Agency;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AgencyRepository extends JpaRepository<Agency, UUID> {
}
