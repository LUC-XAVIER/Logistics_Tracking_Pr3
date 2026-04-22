package com.example.notificationservice.service;

import com.africastalking.AfricasTalking;
import com.africastalking.SmsService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SmsSenderImpl implements SmsSender {

    @Value("${africastalking.username}")
    private String username;

    @Value("${africastalking.api-key}")
    private String apiKey;

    @Value("${africastalking.sender-id}")
    private String senderId;

    private SmsService smsService;

    @PostConstruct
    public void init() {
        AfricasTalking.initialize(username, apiKey);
        smsService = AfricasTalking.getService(AfricasTalking.SERVICE_SMS);
        log.info("Africa's Talking SMS service initialized for username={}", username);
    }

    @Override
    public void send(String recipientPhone, String message) {
        try {
            String formattedPhone = formatPhone(recipientPhone);
            smsService.send(message, senderId, new String[]{formattedPhone}, true);
            log.info("SMS sent successfully to={}", formattedPhone);
        } catch (Exception e) {
            log.error("Failed to send SMS to={}: {}", recipientPhone, e.getMessage());
        }
    }

    private String formatPhone(String phone) {
        if (phone == null || phone.isBlank()) return phone;
        // Remove spaces and dashes
        phone = phone.replaceAll("[\\s\\-]", "");
        // Add Cameroon country code if not present
        if (phone.startsWith("6") || phone.startsWith("2")) {
            return "+237" + phone;
        }
        // Already has country code
        if (!phone.startsWith("+")) {
            return "+" + phone;
        }
        return phone;
    }
}