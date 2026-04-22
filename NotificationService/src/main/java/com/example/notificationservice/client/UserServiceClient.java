package com.example.notificationservice.client;

import com.example.notificationservice.dto.UserContactResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "UserManagementService")
public interface UserServiceClient {

    @GetMapping("/api/v1/users/{userId}/contact")
    UserContactResponse getUserContact(@PathVariable("userId") UUID userId);
}
