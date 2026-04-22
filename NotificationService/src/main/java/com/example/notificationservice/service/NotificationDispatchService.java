package com.example.notificationservice.service;

import com.example.notificationservice.client.UserServiceClient;
import com.example.notificationservice.dto.UserContactResponse;
import com.example.notificationservice.enums.NotificationChannel;
import com.example.notificationservice.enums.NotificationEventType;
import com.example.notificationservice.enums.NotificationStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationDispatchService {

    private final FcmSenderService fcmSenderService;
    private final DeviceTokenService deviceTokenService;
    private final NotificationPersistenceService persistenceService;
    private final UserServiceClient userServiceClient;
    private final EmailSenderService emailSenderService;
    private final SmsSender smsSender;

    public void dispatch(UUID userId, UUID parcelId,
                         String title, String message,
                         NotificationEventType eventType) {

        String recipientEmail = null;
        String recipientPhone = null;

        try {
            UserContactResponse contact = userServiceClient.getUserContact(userId);
            recipientEmail = contact.getEmail();
            recipientPhone = contact.getPhoneNumber();
        } catch (Exception e) {
            log.warn("Could not fetch contact for userId={}: {}", userId, e.getMessage());
        }

        persistenceService.save(userId, parcelId, title, message,
                eventType, NotificationChannel.PUSH,
                recipientEmail, recipientPhone, NotificationStatus.SENT);

        sendPush(userId, title, message);
        sendEmail(recipientEmail, message, eventType);
        sendSms(recipientPhone, title, message);
    }

    private void sendPush(UUID userId, String title, String message) {
        deviceTokenService.getTokenForUser(userId).ifPresentOrElse(
                token -> fcmSenderService.sendPushNotification(token, title, message),
                () -> log.warn("No FCM token found for userId={}", userId)
        );
    }

    private void sendEmail(String recipientEmail, String message,
                           NotificationEventType eventType) {
        if (recipientEmail == null || recipientEmail.isBlank()) {
            log.warn("No email available, skipping email notification");
            return;
        }
        String subject = emailSenderService.resolveSubject(eventType);
        emailSenderService.sendEmail(recipientEmail, subject, message);
    }

    private void sendSms(String recipientPhone, String title, String message) {
        if (recipientPhone == null || recipientPhone.isBlank()) {
            log.warn("No phone number available, skipping SMS notification");
            return;
        }
        String smsBody = title + ": " + message;
        smsSender.send(recipientPhone, smsBody);
    }
}