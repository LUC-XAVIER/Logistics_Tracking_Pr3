package com.example.deliveryservice.client;

import com.example.deliveryservice.dto.old.NotificationResponse;
import com.example.deliveryservice.dto.old.ParcelEventNotificationRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "NotificationService", path = "/logistics/api/v1/notifications")
public interface NotificationServiceClient {

    @PostMapping("/events")
    NotificationResponse sendEventNotification(@RequestBody ParcelEventNotificationRequest request);
}
