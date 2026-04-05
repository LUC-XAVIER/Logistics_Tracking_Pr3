package com.example.deliveryservice.repository;

import com.example.deliveryservice.entity.DeliveryCheckpoint;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeliveryCheckpointRepository extends JpaRepository<DeliveryCheckpoint, UUID> {

    List<DeliveryCheckpoint> findByDeliveryIdOrderBySequenceNumberAsc(UUID deliveryId);
}
