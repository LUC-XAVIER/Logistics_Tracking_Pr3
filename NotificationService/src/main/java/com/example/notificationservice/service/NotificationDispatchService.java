package com.example.notificationservice.service;

import com.example.notificationservice.enums.NotificationChannel;
import com.example.notificationservice.enums.NotificationEventType;
import com.example.notificationservice.enums.NotificationStatus;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationDispatchService {

    private final FcmSenderService fcmSenderService;
    private final DeviceTokenService deviceTokenService;
    private final NotificationPersistenceService persistenceService;

    public void dispatch(UUID userId, UUID parcelId,
                         String title, String message,
                         NotificationEventType eventType,
                         String recipientEmail, String recipientPhone) {

        // 1 — Save to DB (bell icon)
        persistenceService.save(userId, parcelId, title, message,
                eventType, NotificationChannel.PUSH,
                recipientEmail, recipientPhone, NotificationStatus.SENT);

        // 2 — Send FCM push if device token exists
        deviceTokenService.getTokenForUser(userId).ifPresentOrElse(
                token -> fcmSenderService.sendPushNotification(token, title, message),
                () -> log.warn("No FCM token found for userId={}", userId)
        );
    }
}