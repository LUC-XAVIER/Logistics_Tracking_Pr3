package com.example.deliveryservice.repository;

import com.example.deliveryservice.entity.Delivery;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeliveryRepository extends JpaRepository<Delivery, UUID> {

    Optional<Delivery> findByParcelId(UUID parcelId);
}
