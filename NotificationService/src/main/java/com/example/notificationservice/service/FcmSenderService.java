package com.example.notificationservice.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class FcmSenderService {

    public void sendPushNotification(String fcmToken, String title, String body) {
        Message message = Message.builder()
                .setNotification(Notification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .build())
                .setToken(fcmToken)
                .build();
        try {
            String response = FirebaseMessaging.getInstance().send(message);
            log.info("FCM push sent successfully: {}", response);
        } catch (FirebaseMessagingException e) {
            log.error("Failed to send FCM push notification: {}", e.getMessage());
        }
    }
}