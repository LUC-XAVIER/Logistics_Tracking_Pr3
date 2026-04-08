package com.example.logistics_tracking.client;

import com.example.logistics_tracking.dto.old.NotificationResponse;
import com.example.logistics_tracking.dto.old.ParcelEventNotificationRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "NotificationService", path = "/api/v1/notifications")
public interface NotificationServiceClient {

    @PostMapping("/events")
    NotificationResponse sendEventNotification(@RequestBody ParcelEventNotificationRequest request);
}
