package com.example.notificationservice.controller;

import com.example.notificationservice.dto.NotificationResponse;
import com.example.notificationservice.dto.ParcelEventNotificationRequest;
import com.example.notificationservice.dto.SendNotificationRequest;
import com.example.notificationservice.entity.Notification;
import com.example.notificationservice.enums.NotificationChannel;
import com.example.notificationservice.enums.NotificationEventType;
import com.example.notificationservice.enums.NotificationStatus;
import com.example.notificationservice.repository.NotificationRepository;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationController {

    private final NotificationRepository notificationRepository;

    public NotificationController(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @PostMapping
    public ResponseEntity<NotificationResponse> send(@Valid @RequestBody SendNotificationRequest request) {
        Notification notification = notificationRepository.save(Notification.builder()
                .parcelId(request.parcelId())
                .channel(request.channel())
                .eventType(request.eventType())
                .recipientEmail(request.recipientEmail())
                .recipientPhone(request.recipientPhone())
                .message(request.message())
                .status(NotificationStatus.PENDING)
                .build());
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(notification));
    }

    @PostMapping("/events")
    public ResponseEntity<NotificationResponse> sendEvent(@Valid @RequestBody ParcelEventNotificationRequest request) {
        Notification notification = notificationRepository.save(Notification.builder()
                .parcelId(request.parcelId())
                .channel(NotificationChannel.valueOf(request.channel().toUpperCase()))
                .eventType(NotificationEventType.valueOf(request.eventType().toUpperCase()))
                .recipientEmail(request.recipientEmail())
                .recipientPhone(request.recipientPhone())
                .message(request.message())
                .status(NotificationStatus.PENDING)
                .build());
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(notification));
    }

    @GetMapping
    public List<NotificationResponse> list() {
        return notificationRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @GetMapping("/{notificationId}")
    public ResponseEntity<NotificationResponse> get(@PathVariable UUID notificationId) {
        return notificationRepository.findById(notificationId)
                .map(this::toResponse)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    private NotificationResponse toResponse(Notification notification) {
        return new NotificationResponse(
                notification.getId(),
                notification.getParcelId(),
                notification.getChannel(),
                notification.getEventType(),
                notification.getStatus(),
                notification.getRecipientEmail(),
                notification.getRecipientPhone(),
                notification.getMessage(),
                notification.getCreatedAt(),
                notification.getUpdatedAt()
        );
    }
}
