package com.example.notificationservice.repository;

import com.example.notificationservice.entity.DeviceToken;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeviceTokenRepository extends JpaRepository<DeviceToken, UUID> {

    Optional<DeviceToken> findByUserId(UUID userId);

    void deleteByUserId(UUID userId);
}