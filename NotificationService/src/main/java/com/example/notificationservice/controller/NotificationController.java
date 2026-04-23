package com.example.notificationservice.controller;

import com.example.notificationservice.dto.NotificationResponse;
import com.example.notificationservice.dto.UnreadCountResponse;
import com.example.notificationservice.service.NotificationPersistenceService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/logistics/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationPersistenceService persistenceService;

    @GetMapping("/{userId}")
    public ResponseEntity<List<NotificationResponse>> getNotifications(@PathVariable UUID userId) {
        return ResponseEntity.ok(persistenceService.getNotificationsForUser(userId));
    }

    @GetMapping("/{userId}/unread-count")
    public ResponseEntity<UnreadCountResponse> getUnreadCount(@PathVariable UUID userId) {
        return ResponseEntity.ok(persistenceService.getUnreadCount(userId));
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable UUID id) {
        persistenceService.markAsRead(id);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{userId}/read-all")
    public ResponseEntity<Void> markAllAsRead(@PathVariable UUID userId) {
        persistenceService.markAllAsRead(userId);
        return ResponseEntity.ok().build();
    }
}