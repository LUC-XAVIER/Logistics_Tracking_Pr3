package com.example.notificationservice.service;

import com.example.notificationservice.dto.TokenRegistrationRequest;
import com.example.notificationservice.entity.DeviceToken;
import com.example.notificationservice.repository.DeviceTokenRepository;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DeviceTokenService {

    private final DeviceTokenRepository deviceTokenRepository;

    @Transactional
    public void registerToken(TokenRegistrationRequest request) {
        // Replace existing token for this user if present
        deviceTokenRepository.deleteByUserId(request.getUserId());
        DeviceToken token = DeviceToken.builder()
                .userId(request.getUserId())
                .token(request.getToken())
                .platform("WEB")
                .build();
        deviceTokenRepository.save(token);
    }

    public Optional<String> getTokenForUser(UUID userId) {
        return deviceTokenRepository.findByUserId(userId)
                .map(DeviceToken::getToken);
    }
}