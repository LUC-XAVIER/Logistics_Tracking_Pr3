package com.example.notificationservice.client;

import com.example.notificationservice.dto.UserContactResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "UserManagementService", url = "${services.user-service.url}")
public interface UserServiceClient {

    @GetMapping("/api/users/{userId}/contact")
    UserContactResponse getUserContact(@PathVariable("userId") UUID userId);
}