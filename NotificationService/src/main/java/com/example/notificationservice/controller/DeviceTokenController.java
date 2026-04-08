package com.example.notificationservice.controller;

import com.example.notificationservice.dto.TokenRegistrationRequest;
import com.example.notificationservice.service.DeviceTokenService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class DeviceTokenController {

    private final DeviceTokenService deviceTokenService;

    @PostMapping("/register-token")
    public ResponseEntity<Void> registerToken(@Valid @RequestBody TokenRegistrationRequest request) {
        deviceTokenService.registerToken(request);
        return ResponseEntity.ok().build();
    }
}