package com.example.notificationservice.service;

import com.example.notificationservice.enums.NotificationEventType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailSenderService {

    private final JavaMailSender mailSender;

    public void sendEmail(String to, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
            log.info("Email sent successfully to={}", to);
        } catch (Exception e) {
            log.error("Failed to send email to={}: {}", to, e.getMessage());
        }
    }

    public String resolveSubject(NotificationEventType eventType) {
        return switch (eventType) {
            case PARCEL_CREATED -> "Your parcel has been registered";
            case PARCEL_CANCELLED -> "Your parcel has been cancelled";
            case PARCEL_PICKUP_CONFIRMED -> "Your parcel has been picked up";
            case PAYMENT_INITIATED -> "Payment processing";
            case PAYMENT_SUCCESSFUL -> "Payment confirmed";
            case PAYMENT_FAILED -> "Payment failed";
            case PAYMENT_REFUNDED -> "Refund issued";
            case DELIVERY_STARTED -> "Your delivery has started";
            case DELIVERY_PROGRESS -> "Delivery update for your parcel";
            case DELIVERY_COMPLETED -> "Your parcel has been delivered";
            case DELIVERY_FAILED -> "Delivery issue";
            case DELIVERY_RESCHEDULED -> "Delivery rescheduled";
            default -> "Notification from Logistics";
        };
    }
}