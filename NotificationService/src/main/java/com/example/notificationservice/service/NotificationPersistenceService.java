package com.example.notificationservice.service;

import com.example.notificationservice.dto.NotificationResponse;
import com.example.notificationservice.dto.UnreadCountResponse;
import com.example.notificationservice.entity.Notification;
import com.example.notificationservice.enums.NotificationChannel;
import com.example.notificationservice.enums.NotificationEventType;
import com.example.notificationservice.enums.NotificationStatus;
import com.example.notificationservice.repository.NotificationRepository;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationPersistenceService {

    private final NotificationRepository notificationRepository;

    @Transactional
    public void save(UUID userId, UUID parcelId, String title, String message,
                     NotificationEventType eventType, NotificationChannel channel,
                     String recipientEmail, String recipientPhone,
                     NotificationStatus status) {
        Notification notification = Notification.builder()
                .userId(userId)
                .parcelId(parcelId)
                .title(title)
                .message(message)
                .eventType(eventType)
                .channel(channel)
                .recipientEmail(recipientEmail)
                .recipientPhone(recipientPhone)
                .status(status)
                .build();
        notificationRepository.save(notification);
    }

    public List<NotificationResponse> getNotificationsForUser(UUID userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public UnreadCountResponse getUnreadCount(UUID userId) {
        return new UnreadCountResponse(notificationRepository.countByUserIdAndReadFalse(userId));
    }

    @Transactional
    public void markAsRead(UUID notificationId) {
        notificationRepository.findById(notificationId).ifPresent(n -> {
            n.setRead(true);
            notificationRepository.save(n);
        });
    }

    @Transactional
    public void markAllAsRead(UUID userId) {
        notificationRepository.markAllReadByUserId(userId);
    }

    private NotificationResponse toResponse(Notification n) {
        return NotificationResponse.builder()
                .id(n.getId())
                .userId(n.getUserId())
                .parcelId(n.getParcelId())
                .title(n.getTitle())
                .message(n.getMessage())
                .eventType(n.getEventType())
                .channel(n.getChannel())
                .read(n.isRead())
                .createdAt(n.getCreatedAt())
                .build();
    }
}